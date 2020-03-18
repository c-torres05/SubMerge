package com.example.submerge.models;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDBObject {
    //ObjId _id
    //String OwnerId
    //Int subscriptionCount
    //BsonArray Subscriptions
    private final ObjectId _id;
    private final String owner_id;
    private final String user_id;
    private final int subscription_count;
    private final List<Subscription> subscriptions;

    public SubscriptionDBObject(
            final ObjectId id,
            final String owner_id,
            final String user_id,
            final int subscription_count,
            final List<Subscription> subscriptions
    ) {
        this._id = id;
        this.owner_id = owner_id;
        this.user_id = user_id;
        this.subscription_count = subscription_count;
        this.subscriptions = subscriptions;
    }

    public ObjectId get_id() {
        return _id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public String getUser_id() { return user_id; }

    public int getCount() {
        return subscription_count;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public static BsonDocument toBsonDocument(final SubscriptionDBObject item) {
        final BsonDocument asDoc = new BsonDocument();
        asDoc.put(Fields.ID, new BsonObjectId(item.get_id()));
        asDoc.put(Fields.OWNER_ID, new BsonString(item.getOwner_id()));
        asDoc.put(Fields.USER_ID, new BsonString(item.getUser_id()));
        asDoc.put(Fields.SUBSCRIPTION_COUNT, new BsonInt32(item.getCount()));
        BsonArray subs = new BsonArray();
        for (Subscription sub : item.getSubscriptions())
            subs.add(SubscriptionPackager.toBsonDocument(sub));
        asDoc.put(Fields.SUBSCRIPTIONS, new BsonArray(subs));
        return asDoc;
    }

    public static SubscriptionDBObject fromBsonDocument(final BsonDocument doc) {
        List<Subscription> subs = new ArrayList<>();
        for (BsonValue sub : doc.getArray(Fields.SUBSCRIPTIONS)) {
            subs.add(SubscriptionPackager.fromBsonDocument(sub.asDocument()));
        }

        return new SubscriptionDBObject(
                doc.getObjectId(Fields.ID).getValue(),
                doc.getString(Fields.OWNER_ID).getValue(),
                doc.getString(Fields.USER_ID).getValue(),
                doc.getInt32(Fields.SUBSCRIPTION_COUNT).getValue(),
                subs
        );
    }

    static final class Fields {
        static final String ID = "_id";
        static final String OWNER_ID = "owner_id";
        static final String USER_ID = "user_id";
        static final String SUBSCRIPTION_COUNT = "subscription_count";
        static final String SUBSCRIPTIONS = "subscriptions";
    }

    public static final Codec<SubscriptionDBObject> codec = new Codec<SubscriptionDBObject>() {

        @Override
        public void encode(
                final BsonWriter writer, final SubscriptionDBObject value, final EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
        }

        @Override
        public Class<SubscriptionDBObject> getEncoderClass() {
            return SubscriptionDBObject.class;
        }

        @Override
        public SubscriptionDBObject decode(
                final BsonReader reader, final DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return fromBsonDocument(document);
        }
    };
}
