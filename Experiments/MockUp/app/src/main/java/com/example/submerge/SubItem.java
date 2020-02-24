package com.example.submerge;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonTimestamp;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;

class SubItem {
    public static final String SUB_DATABASE = "SubMergeData";
    public static final String SUB_ITEMS_COLLECTION = "Users";

    private final ObjectId _id;
    private final String owner_id;
    private final String name;
    private final String image_url;
    final long renewal;
    final int recurrance;
    private final double currentCost;
    private final BsonArray pastCosts;
//    private final boolean checked;

    /** Constructs a todo item from a MongoDB document. */
    SubItem(
            final ObjectId id,
            final String owner_id,
            final String name,
            final String image_url,
            final long renewal,
            final int recurrance,
            final double currentCost,
            final BsonArray pastCosts
    ) {
        this._id = id;
        this.owner_id = owner_id;
        this.name = name;
        this.image_url = image_url;
        this.renewal = renewal;
        this.recurrance = recurrance;
        this.currentCost = currentCost;
        this.pastCosts = pastCosts;
    }

    public ObjectId get_id() {
        return _id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public String getName() {
        return name;
    }

    public String getImagerUrl() {
        return image_url;
    }

    public long getRenewal() {
        return renewal;
    }

    public int getRecurrance() {
        return recurrance;
    }

    public double getCost() {
        return currentCost;
    }

    public BsonArray getPastCosts() {
        return pastCosts;
    }


    static BsonDocument toBsonDocument(final SubItem item) {
        final BsonDocument asDoc = new BsonDocument();
        final BsonArray pastCosts = new BsonArray();

        asDoc.put(Fields.ID, new BsonObjectId(item.get_id()));
        asDoc.put(Fields.OWNER_ID, new BsonString(item.getOwner_id()));
        asDoc.put(Fields.NAME, new BsonString(item.getName()));
        asDoc.put(Fields.IMAGE_URL, new BsonString(item.getImagerUrl()));
        asDoc.put(Fields.RENEWAL, new BsonDateTime(item.getRenewal()));
        asDoc.put(Fields.RECURRANCE, new BsonInt32(item.getRecurrance()));
        asDoc.put(Fields.COST, new BsonDouble(item.getCost()));
        asDoc.put(Fields.PAST_COSTS, item.getPastCosts());
        return asDoc;
    }

    static SubItem fromBsonDocument(final BsonDocument doc) {
        return new SubItem(
                doc.getObjectId(Fields.ID).getValue(),
                doc.getString(Fields.OWNER_ID).getValue(),
                doc.getString(Fields.NAME).getValue(),
                doc.getString(Fields.IMAGE_URL).getValue(),
                doc.getDateTime(Fields.RENEWAL).getValue(),
                doc.getInt32(Fields.RECURRANCE).getValue(),
                doc.getDouble(Fields.COST).getValue(),
                doc.getArray(Fields.PAST_COSTS)

        );
    }

    static final class Fields {
        static final String ID = "_id";
        static final String OWNER_ID = "owner_id";
        static final String NAME = "name";
        static final String IMAGE_URL = "image_url";
        static final String RENEWAL = "renewal";
        static final String RECURRANCE = "recurrance";
        static final String COST = "cost";
        static final String PAST_COSTS = "past_costs";
    }

    public static final Codec<SubItem> codec = new Codec<SubItem>() {

        @Override
        public void encode(
                final BsonWriter writer, final SubItem value, final EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
        }

        @Override
        public Class<SubItem> getEncoderClass() {
            return SubItem.class;
        }

        @Override
        public SubItem decode(
                final BsonReader reader, final DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return fromBsonDocument(document);
        }
    };
}
