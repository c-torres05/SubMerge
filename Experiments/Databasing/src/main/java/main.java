import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        //Define the connection url to our server and send the request ot connect tot the server.
        String url = "mongodb+srv://admin:SubMerge-admin@submerge-cluster-1-u9n8r.mongodb.net/test?retryWrites=true&w=majority";
        MongoClient mongoClient = MongoClients.create(url);

        //Search for the database 'database-one' in our server
        //If it does not exist, it will be automatically created when data is subsequently added
        MongoDatabase database = mongoClient.getDatabase("database-one");

        //Search for the collection 'Names' inside the database 'database-one'
        //If the collection does not exist, it will also be automatically created when data is put inside of it.
        MongoCollection<Document> collection = database.getCollection("Names");

        //Create a new document object with the k, v pair 'name' & 'Ethan Wolfe
        //All of our data is stored inside JSON form, so the databse itself would look something like:
        /*
        {
            "database-one": {
                "Names": {
                    "<Arbitary Object Id>": {
                        "_id": "<Arbitrary Object Id",
                        "name": "Ethan Wolfe",
                        "versions": "<Arbitrary value>",
                        "info": "<Arbitrary value>"
                    }
                }
            }
        }
         */

        Document doc = new Document("name", "Ethan Wolfe");

        //Insert the single document into the collection using the single insert function.
        collection.insertOne(doc);

    }
}
