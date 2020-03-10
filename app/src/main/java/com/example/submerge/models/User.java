package com.example.submerge.models;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

public class User {
    public static final int UNKNOWN_TYPE = 0;
    public static final int ANON_TYPE = 1;
    public static final int SUBMERGECLIENT_TYPE = 2;
    public static final int GOOGLE_TYPE = 3;
    public static final int FACEBOOK_TYPE = 4;
    public static final int TWITTER_TYPE = 5;

    private final ObjectId _id;
    private final String owner_id;
    private final String user_id;
    private final int type;

    public User(
            final ObjectId id,
            final String owner_id,
            final String user_id,
            final int type
    ) {
        this._id = id;
        this.owner_id = owner_id;
        this.user_id = user_id;
        this.type = type;
    }

    public ObjectId get_id() {
        return _id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public String getUser_Id() {
        return user_id;
    }

    public int getType() {
        return type;
    }


    public static BsonDocument toBsonDocument(final User item) {
        final BsonDocument asDoc = new BsonDocument();
        asDoc.put(Fields.ID, new BsonObjectId(item.get_id()));
        asDoc.put(Fields.OWNER_ID, new BsonString(item.getOwner_id()));
        asDoc.put(Fields.USER_ID, new BsonString(item.getUser_Id()));
        asDoc.put(Fields.TYPE, new BsonInt32(item.getType()));
        return asDoc;
    }

    public static User fromBsonDocument(final BsonDocument doc) {
        return new User(
                doc.getObjectId(Fields.ID).getValue(),
                doc.getString(Fields.OWNER_ID).getValue(),
                doc.getString(Fields.USER_ID).getValue(),
                doc.getInt32(Fields.TYPE).getValue()
        );
    }

    static final class Fields {
        static final String ID = "_id";
        static final String OWNER_ID = "owner_id";
        static final String USER_ID = "user_id";
        static final String TYPE = "type";
    }

    public static final Codec<User> codec = new Codec<User>() {

        @Override
        public void encode(
                final BsonWriter writer, final User value, final EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
        }

        @Override
        public Class<User> getEncoderClass() {
            return User.class;
        }

        @Override
        public User decode(
                final BsonReader reader, final DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return fromBsonDocument(document);
        }
    };
}
