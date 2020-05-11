package com.example.submerge.interfaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.submerge.R;
import com.example.submerge.models.Callback;
import com.example.submerge.models.Result;
import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;
import com.example.submerge.models.requests.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient;
import com.mongodb.stitch.core.StitchServiceException;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoginHandler extends AppCompatActivity {
    Button anonymousLogin;
    Button userLogin;

    EditText username;
    EditText password;
    EditText passwordConfirm;

    DatabaseHandler handler;
    boolean logging_in = false;

    String loginType;
    boolean newUser = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        this.anonymousLogin = findViewById(R.id.anon_login_button);
        this.userLogin = findViewById(R.id.user_login_button);

        this.username = findViewById(R.id.username);
        this.password = findViewById(R.id.password);
        this.passwordConfirm = findViewById(R.id.passwordConfirm);

        this.anonymousLogin.setOnClickListener((View view) -> login(view, "anon"));
        this.userLogin.setOnClickListener((View view) -> login(view, "user-pass"));
    }

    private void login(View view, String type) {
        if (!logging_in) {
            logging_in = true;

            handler = DatabaseHandler.getInstance();
//            MainInterface.setDatabaseHandler(handler);

            switch (type) {
                case "anon":
                    anonLogin();
                    break;
                case "user-pass":
                    userLogin();
                    break;
                case "google":
                    break;
            }
        } else {
            Log.e("SubMerge", "Already logging in!");
        }
    }

    private void anonLogin() {
        handler.loginAnon(result -> {
            Log.d("SubMerger", "Logged in anonymously!");
            if (result.isSuccessful()) {
                loginType = "login-anon";
                gotoMainScreen();
            }
        });
    }

    private void userLogin() {
        if (username.getText().toString().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{3}")) {
            if (password.getText().toString().equals(passwordConfirm.getText().toString())) {
                handler.loginEmail(result -> {
                    Log.d("SubMerge", "Logged in as a user!");
                    if (result.isSuccessful()) {
                        loginType = "login-user";
                        newUser = result.getResult();
                        gotoMainScreen();
                    } else {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), result.getFailure(), duration);
                        toast.show();
                        logging_in = false;
                    }
                }, username.getText().toString(), password.getText().toString());
            } else {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), "Passwords do not match!", duration);
                toast.show();
                logging_in = false;
            }
        } else {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid email!", duration);
            toast.show();
            logging_in = false;
        }
    }

    private void addUser(Callback<User, String> callback) {
        User user = new User(new ObjectId(), handler.getUserId(), handler.getName(), User.ANON_TYPE);

        handler.addUser(new Request(user, null), result -> {
            Log.i("SubMerge", "Added user!");
            callback.onComplete(new Result<>(user, "", result.isSuccessful()));
        });
    }

    private void gotoMainScreen() {
        Intent main = new Intent(this, MainInterface.class);
        handler.finishInit();
        if (newUser) {
            addUser(result -> {
                if (result.isSuccessful()) {
                    Calendar cal = Calendar.getInstance();
//                    List<Request> add_requests = new ArrayList<>();
//                    add_requests.add(new Request(result.getResult(), new Subscription("netflix", "Netflix", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 12.99, 0.00, "https://www.netflix.com/login")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("hulu", "Hulu", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 5.99, 0.00, "https://help.hulu.com/s/article/cancel-hulu-subscription?language=en_US")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("adobe", "Adobe", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 52.99, 0.00, "https://helpx.adobe.com/manage-account/using/cancel-subscription.html?promoid=CW7623QN&mv=other")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("amazonprime", "Amazon Prime", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 12.99, 0.00, "https://www.amazon.com/gp/help/customer/display.html?nodeId=GNQFBWDZJN838JZF")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("applemusic", "Apple Music", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 9.99, 0.00, "https://support.apple.com/en-us/HT202039")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("appletvplus", "AppleTV+", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 4.99, 0.00, "https://support.apple.com/guide/tv/subscriptions-atvb0d233668/tvos")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("blueapron", "Blue Apron", false, cal.getTime(), Subscription.Recurrences.WEEKLY, 59.94, 0.00, "https://www.blueapron.com/users/sign_in")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("disney", "Disney+", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 6.99, 0.00, "https://www.disneyplus.com/login")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("dropbox", "DropBox", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 9.99, 0.00, "https://help.dropbox.com/accounts-billing/cancellations-refunds/cancel-mobile")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("hbonow", "HBO Now", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 19.99, 0.00, "https://help.hbonow.com/Answer/Detail/206#cancel")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("ipsy", "Ipsy", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 10.00, 0.00, "https://help.ipsy.com/en_us/how-do-i-cancel-my-membership-B1nFSt4Vr")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("kindleunlimited", "Kindle Unlimited", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 9.99, 0.00, "https://www.amazon.com/gp/help/customer/display.html?nodeId=GLSQ4722655M4ZEJ")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("pandora", "Pandora", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 9.99, 0.00, "https://help.pandora.com/s/article/Cancel-your-Subscription-1519949295562?language=en_US")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("soundcloud", "Sound Cloud", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 4.99, 0.00, "https://help.soundcloud.com/hc/en-us/articles/115003562888")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("spotify", "Spotify", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 9.99, 0.00, "https://support.spotify.com/us/article/how-to-cancel-your-subscription/")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("youtube", "Youtube", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 12.99, 0.00, "https://support.google.com/youtube/answer/6308278?co=GENIE.Platform%3DAndroid&hl=en")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("manscaped", "Manscaped", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 14.99, 0.00, "https://www.manscaped.com/account/login")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("bulubox", "Bulu Box", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 29.99, 0.00, "https://www.bulugroup.com")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("candyclub", "Candy Club", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 29.99, 0.00, "https://www.candyclub.com")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("munchpak", "MunchPak", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 22.95, 0.00, "https://munchpak.com/faq")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("lovewithfood", "Love With Food", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 7.99, 0.00, "https://lovewithfood.com/sign_in")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("urthbox", "UrthBox", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 14.99, 0.00, "https://www.urthbox.com/customer_login.php")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("lipmonthly", "Lip Monthly", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 12.95, 0.00, "https://lipmonthly.com/account/login?return_url=%2Faccount")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("naturebox", "Nature Box", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 24.99, 0.00, "https://support.naturebox.com/hc/en-us/articles/212404368-How-do-I-cancel-my-account-")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("winc", "Winc", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 39.99, 0.00, "https://support.winc.com/hc/en-us/articles/222813107-Can-I-cancel-my-membership-")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("birchbox", "Birch Box", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 13.00, 0.00, "https://support.birchbox.com/hc/en-us/articles/360031765091-Subscription-Plan-Cancellations")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("twitchprime", "Twitch Prime", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 10.99, 0.00, "https://help.twitch.tv/s/article/how-to-use-twitch-prime-subscriptions?language=en_US#FrequentlyAskedQuestions")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("carnivoreclub", "Carnivore Club", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 39.99, 0.00, "https://us.carnivoreclub.co/account/login")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("amcpremiere", "AMC Premiere", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 4.99, 0.00, "https://www.amc.com/account/")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("nintendo", "Switch Online", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 3.99, 0.00, "https://en-americas-support.nintendo.com/app/answers/detail/a_id/41191/~/can-i-cancel-my-nintendo-switch-online-membership%3F")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("switch_logo", "Switch", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 29.00, 0.00, "https://joinswitch.com/#")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("foodstirs", "Foodstirs", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 24.99, 0.00, "https://foodstirs.com/account/login")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("cairn", "Cairn", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 59.90, 0.00, "https://www.getcairn.com/account/login?return_url=%2Faccount")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("quirkycrate", "Quicky Crate", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 36.65, 0.00, "https://www.quirkycrate.com/account/login")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("dollarshaveclub", "Dollar Shave Club", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 40.00, 0.00, "https://www.dollarshaveclub.com/login?dsc_source=dsc&dsc_medium=header_nav")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("accio", "Accio", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 39.99, 0.00, "https://acciobox.cratejoy.com/customer/login")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("homechef", "Home Chef", false, cal.getTime(), Subscription.Recurrences.WEEKLY, 6.99, 0.00, "https://www.homechef.com/users/sign_in")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("stickii", "Stickii", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 10.00, 0.00, "https://members.stickii.club/customer/login")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("sayitwithasock", "Say It With A Sock", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 12.99, 0.00, "https://sayitwithasock.com/account")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("feelingfab", "Feeling Gab", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 21.95, 0.00, "https://www.feelingfabbox.com/customer/login")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("enchantmentbox", "Enchantment Box", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 39.95, 0.00, "https://boxes.hellosubscription.com/my-account/")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("inspiredbookclub", "Inspired Book Club", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 34.99, 0.00, "https://boxes.hellosubscription.com/my-account/")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("meundies", "Me Undies", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 16.00, 0.00, "https://www.meundies.com/?login=open")));
//                    add_requests.add(new Request(result.getResult(), new Subscription("otakubox", "Oktaku Box", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 24.95, 0.00, "https://www.subbly.co/account/auth/login?store=theotakubox")));
//
//                    for (Request request : add_requests) {
//                        handler.insertSearchSubscription(request, r -> {
//                        });
//                    }
                    User.encode_intent(main, result.getResult());
                    main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    main.putExtra("from", loginType);
                    startActivity(main);
                }
            });
        } else {
            User user = new User(null, handler.getUserId(), handler.getName(), User.ANON_TYPE);
            handler.findUser(new Request(user, null), result -> {
                if (result.isSuccessful()) {
                    Log.i("SubMerge", "Added user!");
                    User.encode_intent(main, result.getResult());
                    main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    main.putExtra("from", loginType);
                    startActivity(main);
                }
            });
        }
    }
}
