import io.mysocialapp.client.MySocialApp;
import io.mysocialapp.client.Session;
import io.mysocialapp.client.models.Account;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by evoxmusic on 14/10/2019.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("The first argument must be 'APP_ID' and the second your 'API access token'");
        }

        final String appId = args[0];
        final String accessToken = args[1];

        final MySocialApp msa = new MySocialApp.Builder()
                .setAppId(appId)
                .build();

        final Session session = msa.blockingConnect(accessToken);


        final File file = new File("." + File.separator + "msa_" + new Date().getTime() + ".csv");
        FileWriter writer = new FileWriter(file, true);

        writer.write("id;first name;last name;email;external id;last connection date");
        writer.write("\n");

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        session.getUser().blockingStream(10).forEach(users -> {
            users.getUsers().forEach(user -> {
                final Account acc = user.blockingConnectAsUser().getAccount().blockingGet();

                try {
                    writer.write(String.format("%s;%s;%s;%s;%s;%s", acc.getIdStr(), acc.getFirstName(),
                            acc.getLastName(), acc.getEmail(), acc.getExternalId(),
                            sdf.format(acc.getUserStat().getStatus().getLastConnectionDate())));

                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        });

        writer.close();
    }
}
