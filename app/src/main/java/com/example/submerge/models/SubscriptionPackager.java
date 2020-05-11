package com.example.submerge.models;

import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class SubscriptionPackager {
    static final class Fields {
        static final String ID = "_id";
        static final String OWNER_ID = "owner_id";
        static final String TITLE = "name";
        static final String COST = "cost";
        static final String CHANGE = "change";
        static final String TRIAL = "trial";
        static final String RENEWAL = "renewal";
        static final String RECURRANCE = "recurrance";
        static final String IMAGE = "image";
        static final String URL = "url";
    }

    public static BsonDocument toBsonDocument(final Subscription item) {
        final BsonDocument asDoc = new BsonDocument();
        asDoc.put(Fields.ID, new BsonObjectId(item.accessId()));
        asDoc.put(Fields.OWNER_ID, new BsonString(item.accessOwnerId()));
        asDoc.put(Fields.TITLE, new BsonString(item.accessTitle()));
        asDoc.put(Fields.COST, new BsonDouble(item.accessCost()));
        asDoc.put(Fields.CHANGE, new BsonDouble(item.accessChange()));
        asDoc.put(Fields.TRIAL, new BsonBoolean(item.accessTrial()));
        asDoc.put(Fields.RENEWAL, new BsonDateTime(item.accessRenewal()));
        asDoc.put(Fields.RECURRANCE, new BsonInt32(item.accessRecurrance()));
        asDoc.put(Fields.IMAGE, new BsonString(item.accessImage()));
        asDoc.put(Fields.URL, new BsonString(item.accessURL()));
        return asDoc;
    }

    public static Subscription fromBsonDocument(final BsonDocument doc) {
        return new Subscription(
                doc.getObjectId(Fields.ID).getValue(),
                doc.getString(Fields.OWNER_ID).getValue(),
                doc.getString(Fields.IMAGE).getValue(),
                doc.getString(Fields.TITLE).getValue(),
                doc.getBoolean(Fields.TRIAL).getValue(),
                doc.getDateTime(Fields.RENEWAL).getValue(),
                doc.getInt32(Fields.RECURRANCE).getValue(),
                doc.getDouble(Fields.COST).getValue(),
                doc.getDouble(Fields.CHANGE).getValue(),
                doc.getString(Fields.URL).getValue()
        );
    }

    public static final Codec<Subscription> codec = new Codec<Subscription>() {

        @Override
        public void encode(
                final BsonWriter writer, final Subscription value, final EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
        }

        @Override
        public Class<Subscription> getEncoderClass() {
            return Subscription.class;
        }

        @Override
        public Subscription decode(
                final BsonReader reader, final DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return fromBsonDocument(document);
        }
    };
}
