package com.example.submerge.models;

import android.content.Intent;

import androidx.annotation.NonNull;

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

import java.util.Objects;

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

    public static User decode_intent(Intent intent) {
        ObjectId object_id = new ObjectId(Objects.requireNonNull(intent.getStringExtra("user_object_id")));
        String owner_id = intent.getStringExtra("user_owner_id");
        String user_id = intent.getStringExtra("user_user_id");
        int type = intent.getIntExtra("user_type", User.UNKNOWN_TYPE);
        return new User(object_id, owner_id, user_id, type);
    }

    public static void encode_intent(Intent intent, User user) {
        intent.putExtra("user_object_id", user.get_id().toString());
        intent.putExtra("user_owner_id", user.getOwner_id());
        intent.putExtra("user_user_id", user.getUser_Id());
        intent.putExtra("user_type", user.getType());
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

    @NonNull
    @Override
    public String toString() {
        String result = "\n";
        result += "User: ";
        result += "\t_id: " + get_id();
        result += "\towner_id: " + getOwner_id();
        result += "\tuser_id: " + getUser_Id();
        result += "\n";
        return result;
    }
}
