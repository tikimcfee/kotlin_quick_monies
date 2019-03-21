package kotlin_quick_monies.functionality.commands

/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

import java.io.IOException
import java.lang.reflect.Type
import java.util.ArrayList
import java.util.Collections

/**
 * A JsonAdapter factory for objects that include type information in the JSON. When decoding JSON
 * Moshi uses this type information to determine which class to decode to. When encoding Moshi uses
 * the object’s class to determine what type information to include.
 *
 *
 * Suppose we have an interface, its implementations, and a class that uses them:
 *
 * <pre> `interface HandOfCards {
 * }
 *
 * class BlackjackHand extends HandOfCards {
 * Card hidden_card;
 * List<Card> visible_cards;
 * }
 *
 * class HoldemHand extends HandOfCards {
 * Set<Card> hidden_cards;
 * }
 *
 * class Player {
 * String name;
 * HandOfCards hand;
 * }
`</pre> *
 *
 *
 * We want to decode the following JSON into the player model above:
 *
 * <pre> `{
 * "name": "Jesse",
 * "hand": {
 * "hand_type": "blackjack",
 * "hidden_card": "9D",
 * "visible_cards": ["8H", "4C"]
 * }
 * }
`</pre> *
 *
 *
 * Left unconfigured, Moshi would incorrectly attempt to decode the hand object to the abstract
 * `HandOfCards` interface. We configure it to use the appropriate subtype instead:
 *
 * <pre> `Moshi moshi = new Moshi.Builder()
 * .add(PolymorphicJsonAdapterFactory.of(HandOfCards.class, "hand_type")
 * .withSubtype(BlackjackHand.class, "blackjack")
 * .withSubtype(HoldemHand.class, "holdem"))
 * .build();
`</pre> *
 *
 *
 * This class imposes strict requirements on its use:
 *
 *
 *  * Base types may be classes or interfaces.
 *  * Subtypes must encode as JSON objects.
 *  * Type information must be in the encoded object. Each message must have a type label like
 * `hand_type` whose value is a string like `blackjack` that identifies which type
 * to use.
 *  * Each type identifier must be unique.
 *
 *
 *
 * For best performance type information should be the first field in the object. Otherwise Moshi
 * must reprocess the JSON stream once it knows the object's type.
 *
 *
 * If an unknown subtype is encountered when decoding, this will throw a [ ]. If an unknown type is encountered when encoding, this will throw an [ ].
 *
 *
 * If you want to specify a custom unknown fallback for decoding, you can do so via
 * [.withDefaultValue]. This instance should be immutable, as it is shared.
 */
class PolymorphicJsonAdapterFactory<T> internal constructor(
        internal val baseType: Class<T>,
        internal val labelKey: String,
        internal val labels: List<String>,
        internal val subtypes: List<Type>,
        internal val defaultValue: T?,
        internal val defaultValueSet: Boolean) : JsonAdapter.Factory {

    /**
     * Returns a new factory that decodes instances of `subtype`. When an unknown type is found
     * during encoding an [IllegalArgumentException] will be thrown. When an unknown label
     * is found during decoding a [JsonDataException] will be thrown.
     */
    fun withSubtype(subtype: Class<out T>?, label: String?): PolymorphicJsonAdapterFactory<T> {
        if (subtype == null) throw NullPointerException("subtype == null")
        if (label == null) throw NullPointerException("label == null")
        if (labels.contains(label) || subtypes.contains(subtype)) {
            throw IllegalArgumentException("Subtypes and labels must be unique.")
        }
        val newLabels = ArrayList(labels)
        newLabels.add(label)
        val newSubtypes = ArrayList(subtypes)
        newSubtypes.add(subtype)
        return PolymorphicJsonAdapterFactory(baseType,
                labelKey,
                newLabels,
                newSubtypes,
                defaultValue,
                defaultValueSet)
    }

    /**
     * Returns a new factory that with default to `defaultValue` upon decoding of unrecognized
     * labels. The default value should be immutable.
     */
    fun withDefaultValue(defaultValue: T?): PolymorphicJsonAdapterFactory<T> {
        return PolymorphicJsonAdapterFactory(baseType,
                labelKey,
                labels,
                subtypes,
                defaultValue,
                true)
    }

    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || !annotations.isEmpty()) {
            return null
        }

        val jsonAdapters = ArrayList<JsonAdapter<Any>>(subtypes.size)
        var i = 0
        val size = subtypes.size
        while (i < size) {
            jsonAdapters.add(moshi.adapter(subtypes[i]))
            i++
        }

        return PolymorphicJsonAdapter(labelKey,
                labels,
                subtypes,
                jsonAdapters,
                defaultValue,
                defaultValueSet
        ).nullSafe()
    }

    internal class PolymorphicJsonAdapter(val labelKey: String,
                                          val labels: List<String>,
                                          val subtypes: List<Type>,
                                          val jsonAdapters: List<JsonAdapter<Any>>,
                                          val defaultValue: Any?,
                                          val defaultValueSet: Boolean) : JsonAdapter<Any>() {

        /** Single-element options containing the label's key only.  */
        val labelKeyOptions: JsonReader.Options
        /** Corresponds to subtypes.  */
        val labelOptions: JsonReader.Options

        init {

            this.labelKeyOptions = JsonReader.Options.of(labelKey)
            this.labelOptions = JsonReader.Options.of(*labels.toTypedArray())
        }
        
        override fun fromJson(reader: JsonReader): Any? {
            val peeked = reader.peekJson()
            peeked.setFailOnUnknown(false)
            val labelIndex: Int
            try {
                labelIndex = labelIndex(peeked)
            } finally {
                peeked.close()
            }
            if (labelIndex == -1) {
                reader.skipValue()
                return defaultValue
            }
            return jsonAdapters[labelIndex].fromJson(reader)
        }
        
        private fun labelIndex(reader: JsonReader): Int {
            reader.beginObject()
            while (reader.hasNext()) {
                if (reader.selectName(labelKeyOptions) == -1) {
                    reader.skipName()
                    reader.skipValue()
                    continue
                }

                val labelIndex = reader.selectString(labelOptions)
                if (labelIndex == -1 && !defaultValueSet) {
                    throw JsonDataException("Expected one of "
                            + labels
                            + " for key '"
                            + labelKey
                            + "' but found '"
                            + reader.nextString()
                            + "'. Register a subtype for this label.")
                }
                return labelIndex
            }

            throw JsonDataException("Missing label for $labelKey")
        }
        
        override fun toJson(writer: JsonWriter, value: Any?) {
            val type = value?.javaClass ?: return
            val labelIndex = subtypes.indexOf(type)
            if (labelIndex == -1) {
                throw IllegalArgumentException("Expected one of "
                        + subtypes
                        + " but found "
                        + value
                        + ", a "
                        + value.javaClass
                        + ". Register this subtype.")
            }
            val adapter = jsonAdapters[labelIndex]
            writer.beginObject()
            writer.name(labelKey).value(labels[labelIndex])
            val flattenToken = writer.beginFlatten()
            adapter.toJson(writer, value)
            writer.endFlatten(flattenToken)
            writer.endObject()
        }

        override fun toString(): String {
            return "PolymorphicJsonAdapter($labelKey)"
        }
    }

    companion object {

        /**
         * @param baseType The base type for which this factory will create adapters. Cannot be Object.
         * @param labelKey The key in the JSON object whose value determines the type to which to map the
         * JSON object.
         */
        fun <T> of(baseType: Class<T>?, labelKey: String?): PolymorphicJsonAdapterFactory<T> {
            if (baseType == null) throw NullPointerException("baseType == null")
            if (labelKey == null) throw NullPointerException("labelKey == null")
            return PolymorphicJsonAdapterFactory(
                    baseType,
                    labelKey,
                    emptyList(),
                    emptyList(), null,
                    false)
        }
    }
}
