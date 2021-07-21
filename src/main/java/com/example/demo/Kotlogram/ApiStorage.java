package com.example.demo.Kotlogram;

import com.example.demo.Model.MyAuthKey;
import com.example.demo.Model.MySession;
import com.example.demo.Repositroy.MyAuthKeyRepository;
import com.example.demo.Repositroy.MySessionRepository;
import com.github.badoualy.telegram.api.TelegramApiStorage;
import com.github.badoualy.telegram.mtproto.auth.AuthKey;
import com.github.badoualy.telegram.mtproto.model.DataCenter;
import com.github.badoualy.telegram.mtproto.model.MTSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Scope("prototype")
public class ApiStorage implements TelegramApiStorage {

    private String PHONE_NUMBER;

    private final MyAuthKeyRepository myAuthKeyRepository;
    private final MySessionRepository mySessionRepository;


    //Create File variable for auth.key and dc.save
    public  final File AUTH_KEY_FILE = new File("Properties/auth" + ".key");
//    public  final File NEAREST_DC_FILE;
//    public  final File SESSION_FILE;

    public ApiStorage(MyAuthKeyRepository myAuthKeyRepository, MySessionRepository mySessionRepository) {
//        this.AUTH_KEY_FILE = new File("Properties/auth" + PHONE_NUMBER + ".key");
//        this.NEAREST_DC_FILE = new File("Properties/dc" + PHONE_NUMBER + ".save");
//        this.SESSION_FILE = new File("Properties/ses" + PHONE_NUMBER + ".json");

        this.myAuthKeyRepository = myAuthKeyRepository;
        this.mySessionRepository = mySessionRepository;
    }

    public void setPHONE_NUMBER(String PHONE_NUMBER) {
        this.PHONE_NUMBER = PHONE_NUMBER;
    }

    public String getPHONE_NUMBER() {
        return PHONE_NUMBER;
    }

    @Override
    public void saveAuthKey(@NotNull AuthKey authKey) {
//        try {
//            FileUtils.writeByteArrayToFile(AUTH_KEY_FILE, authKey.getKey());
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.writeValue(AUTH_KEY_FILE, authKey);
        MyAuthKey myAuthKey = new MyAuthKey();
        myAuthKey.setPhoneNumber(PHONE_NUMBER);
        myAuthKey.setKey(authKey.getKey());
        myAuthKeyRepository.save(myAuthKey);

//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Nullable
    @Override
    public AuthKey loadAuthKey() {
//        try {
//            return new AuthKey(FileUtils.readFileToByteArray(AUTH_KEY_FILE));
//
////            ObjectMapper objectMapper = new ObjectMapper();
////            return objectMapper.readValue(AUTH_KEY_FILE, AuthKey.class);
//        } catch (IOException e) {
//            if (!(e instanceof FileNotFoundException))
//                e.printStackTrace();
//        }
        MyAuthKey myAuthKey = myAuthKeyRepository.findAllByPhoneNumber(PHONE_NUMBER).orElse(null);
        if(myAuthKey == null) {
            return null;
        }
        return new AuthKey(myAuthKey.getKey());
    }

    @Override
    public void saveDc(@NotNull DataCenter dataCenter) {
//        try {
//            FileUtils.write(NEAREST_DC_FILE, dataCenter.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Nullable
    @Override
    public DataCenter loadDc() {
//        try {
//            String[] infos = FileUtils.readFileToString(NEAREST_DC_FILE).split(":");
//            return new DataCenter(infos[0], Integer.parseInt(infos[1]));
//        } catch (IOException e) {
//            if (!(e instanceof FileNotFoundException))
//                e.printStackTrace();
//        }

        return new DataCenter("149.154.167.51", 443);
    }

    @Override
    public void deleteAuthKey() {
//        try {
//            FileUtils.forceDelete(AUTH_KEY_FILE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void deleteDc() {
//        try {
//            FileUtils.forceDelete(NEAREST_DC_FILE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void saveSession(@Nullable MTSession session) {
        if(session != null) {
            MySession mySession = new MySession();
            mySession.setSession_id(session.getId());
            mySession.setContentRelatedCount(session.getContentRelatedCount());
            mySession.setSalt(session.getSalt());
            mySession.setLastMessageId(session.getLastMessageId());
            mySession.setTag(session.getTag());
            mySession.setDataCenterIp(session.getDataCenter().getIp());
            mySession.setDataCenterPort(session.getDataCenter().getPort());
            mySession.setMarkerName(session.getMarker().getName());
            mySession.setPhoneNumber(PHONE_NUMBER);
            mySessionRepository.save(mySession);
        }

    }

    @Nullable
    @Override
    public MTSession loadSession() {
        MySession session = mySessionRepository.findAllByPhoneNumber(PHONE_NUMBER).orElse(null);
        if(session != null) {
            return new MTSession(new DataCenter(session.getDataCenterIp(), session.getDataCenterPort()),
                    session.getSession_id(), session.getSalt(), session.getContentRelatedCount(), session.getLastMessageId(),
                    session.getTag());
        }

        return null;
    }
}
