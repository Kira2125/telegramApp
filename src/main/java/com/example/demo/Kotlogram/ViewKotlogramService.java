package com.example.demo.Kotlogram;

import com.example.demo.Utils;
import com.github.badoualy.telegram.api.Kotlogram;
import com.github.badoualy.telegram.api.TelegramClient;
import com.github.badoualy.telegram.tl.api.TLAbsInputPeer;
import com.github.badoualy.telegram.tl.api.TLAbsMessage;
import com.github.badoualy.telegram.tl.api.TLAbsMessageAction;
import com.github.badoualy.telegram.tl.api.TLAbsPeer;
import com.github.badoualy.telegram.tl.api.TLAbsUser;
import com.github.badoualy.telegram.tl.api.TLChannel;
import com.github.badoualy.telegram.tl.api.TLChannelForbidden;
import com.github.badoualy.telegram.tl.api.TLChat;
import com.github.badoualy.telegram.tl.api.TLChatEmpty;
import com.github.badoualy.telegram.tl.api.TLChatForbidden;
import com.github.badoualy.telegram.tl.api.TLImportedContact;
import com.github.badoualy.telegram.tl.api.TLInputPeerChannel;
import com.github.badoualy.telegram.tl.api.TLInputPeerChat;
import com.github.badoualy.telegram.tl.api.TLInputPeerEmpty;
import com.github.badoualy.telegram.tl.api.TLInputPeerSelf;
import com.github.badoualy.telegram.tl.api.TLInputPeerUser;
import com.github.badoualy.telegram.tl.api.TLInputPhoneContact;
import com.github.badoualy.telegram.tl.api.TLMessage;
import com.github.badoualy.telegram.tl.api.TLMessageService;
import com.github.badoualy.telegram.tl.api.TLPeerChannel;
import com.github.badoualy.telegram.tl.api.TLPeerChat;
import com.github.badoualy.telegram.tl.api.TLPeerUser;
import com.github.badoualy.telegram.tl.api.TLUser;
import com.github.badoualy.telegram.tl.api.auth.TLAuthorization;
import com.github.badoualy.telegram.tl.api.auth.TLSentCode;
import com.github.badoualy.telegram.tl.api.contacts.TLImportedContacts;
import com.github.badoualy.telegram.tl.api.messages.TLAbsDialogs;
import com.github.badoualy.telegram.tl.api.messages.TLAbsMessages;
import com.github.badoualy.telegram.tl.core.TLObject;
import com.github.badoualy.telegram.tl.core.TLVector;
import com.github.badoualy.telegram.tl.exception.RpcErrorException;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

@Service
public class ViewKotlogramService {
    ApiStorage authenticateApiStorage;


    @Lookup
    public ApiStorage createApiStorage() {
        return null;
    };

    public void sendVerificationCode(String phone) {
        ApiStorage apiStorage = createApiStorage();
        apiStorage.setPHONE_NUMBER(phone);
        TelegramClient client = Kotlogram.getDefaultClient(Utils.application, apiStorage);

        try {
            TLSentCode sentCode = client.authSendCode(false, apiStorage.getPHONE_NUMBER(), true);
            apiStorage.saveCodeHash(sentCode.getPhoneCodeHash());
            authenticateApiStorage = apiStorage;

        } catch (RpcErrorException | IOException e) {
            e.printStackTrace();
        } finally {
            client.close(false);
        }
    }

    public void submitVerificationCode(String code, String phone) {
        ApiStorage apiStorage = authenticateApiStorage;

        TelegramClient client = Kotlogram.getDefaultClient(Utils.application, apiStorage);
        apiStorage.setPHONE_NUMBER(phone);
        try {
            String codeHash = apiStorage.loadCodeHash();
            TLAuthorization authorization = client.authSignIn(phone, codeHash, code);
            TLUser self = authorization.getUser().getAsUser();
            System.out.println("You are now signed in as " + self.getFirstName() + " " + self.getLastName() + " @" + self.getUsername());
            apiStorage.saveAuthKeyHandler();
        } catch (RpcErrorException | IOException e) {
            e.printStackTrace();
        } finally {

            client.close(false);
        }
    }

    private void authorization(TelegramClient client, ApiStorage apiStorage) throws RpcErrorException, IOException {
        TLSentCode sentCode = client.authSendCode(false, apiStorage.getPHONE_NUMBER(), true);
        System.out.println("Authentication code: ");
        String code = new Scanner(System.in).nextLine();

        // Auth with the received code
        TLAuthorization authorization = client.authSignIn(apiStorage.getPHONE_NUMBER(), sentCode.getPhoneCodeHash(), code);
        TLUser self = authorization.getUser().getAsUser();
        System.out.println("You are now signed in as " + self.getFirstName() + " " + self.getLastName() + " @" + self.getUsername());
        apiStorage.saveAuthKeyHandler();

    }

    private TLAbsInputPeer getInputPeer(TLAbsDialogs tlAbsDialogs) {
        TLAbsPeer tlAbsPeer = tlAbsDialogs.getDialogs().get(0).getPeer();
        int peerId = getId(tlAbsPeer);
        TLObject peer = tlAbsPeer instanceof TLPeerUser ?
                tlAbsDialogs.getUsers().stream().filter(user -> user.getId() == peerId).findFirst().get()
                : tlAbsDialogs.getChats().stream().filter(chat -> chat.getId() == peerId).findFirst().get();

        if (peer instanceof TLChannel)
            return new TLInputPeerChannel(((TLChannel) peer).getId(), ((TLChannel) peer).getAccessHash());
        if (peer instanceof TLChat)
            return new TLInputPeerChat(((TLChat) peer).getId());
        if (peer instanceof TLUser)
            return new TLInputPeerUser(((TLUser) peer).getId(), ((TLUser) peer).getAccessHash());

        return new TLInputPeerEmpty();
    }


    private int getId(TLAbsPeer peer) {
        if (peer instanceof TLPeerUser)
            return ((TLPeerUser) peer).getUserId();
        if (peer instanceof TLPeerChat)
            return ((TLPeerChat) peer).getChatId();

        return ((TLPeerChannel) peer).getChannelId();
    }

    public void sendMessage(String phone) {
        ApiStorage apiStorage = createApiStorage();
        apiStorage.setPHONE_NUMBER(phone);
        TelegramClient client = Kotlogram.getDefaultClient(Utils.application, apiStorage);
        try {
//            if (apiStorage.loadSession() == null) {
//                    authorization(client, apiStorage);
//            }
            sendMessage(client);
        } catch (RpcErrorException | IOException e) {
            e.printStackTrace();
        } finally {
            client.close(false);
        }
    }

    private void sendMessage(TelegramClient client) throws RpcErrorException, IOException {


            Random random = new Random();

            TLVector<TLInputPhoneContact> vector = new TLVector<>();
            TLInputPhoneContact contact = new TLInputPhoneContact(Math.abs(random.nextLong()), "+7",
                    "firs-name", "last-name");
            vector.add(contact);
            TLImportedContacts importContacts = client.contactsImportContacts(vector, true);

            TLImportedContact importedContact = importContacts.getImported().stream().findFirst().orElse(null);

            TLInputPeerUser inputPeerUser = new TLInputPeerUser();
            inputPeerUser.setUserId(importedContact.getUserId());

            //you can save importedContact.getUserId() into db and use it as many as you want

            client.messagesSendMessage(inputPeerUser, "some message", Math.abs(new Random().nextLong()));

    }

    public StringBuilder getRecentConversationList(String phone) {
        StringBuilder stringBuilder = new StringBuilder();
        ApiStorage apiStorage = createApiStorage();
        apiStorage.setPHONE_NUMBER(phone);
        TelegramClient client = Kotlogram.getDefaultClient(Utils.application, apiStorage);
        try {
//            if (apiStorage.loadAuthKey() == null) {
//
//                    authorization(client, apiStorage);
//            }
            stringBuilder = getRecentConversationList(client);
//            client.authLogOut();
//            apiStorage.deleteAuthKey();
        } catch (RpcErrorException | IOException e) {
            e.printStackTrace();
        } finally {
            client.close(false);
        }
        return stringBuilder;
    }

    private StringBuilder getRecentConversationList(TelegramClient client) throws RpcErrorException, IOException {
        // Number of recent conversation you want to get (Telegram has an internal max, your value will be capped)
        int count = 10;
        StringBuilder stringBuilder = new StringBuilder();
        // You can start making requests
            TLAbsDialogs tlAbsDialogs = client.messagesGetDialogs(false, 3, 0, new TLInputPeerSelf(), count);
//            TLAbsInputPeer inputPeer = getInputPeer(tlAbsDialogs);
//            TLAbsMessages tlAbsMessages = client.messagesGetHistory(inputPeer, 0, 0, 0, count, 0, 0);
//            tlAbsMessages.getMessages().forEach(message -> {
//                if (message instanceof TLMessage)
//                    System.out.println(((TLMessage) message).getMessage());
//                else
//                    System.out.println("Service message");
//            });
            // Map peer id to displayable string
            HashMap<Integer, String> nameMap = createNameMap(tlAbsDialogs);

            // Map message id to message
            HashMap<Integer, TLAbsMessage> messageMap = new HashMap<>();
            tlAbsDialogs.getMessages().forEach(message -> messageMap.put(message.getId(), message));

            tlAbsDialogs.getDialogs().forEach(dialog -> {
                String dialogPeer = nameMap.get(getId(dialog.getPeer())) + ": ";
                stringBuilder.append("*****" + dialogPeer + " ");
                TLAbsMessage topMessage = messageMap.get(dialog.getTopMessage());
                if (topMessage instanceof TLMessage) {
                    // The message could also be a file, a photo, a gif, ...
                    stringBuilder.append(topMessage.getId() + " " + ((TLMessage) topMessage).getMediaUnread() + " ");
                    stringBuilder.append(((TLMessage) topMessage).getMessage());
                } else if (topMessage instanceof TLMessageService) {
                    TLAbsMessageAction action = ((TLMessageService) topMessage).getAction();
                    // action defined the type of message (user joined group, ...)
                    stringBuilder.append("Service message");
                }
            });

        return stringBuilder;
    }

    public StringBuilder getMessages(String phone) {
        StringBuilder stringBuilder = new StringBuilder();
        ApiStorage apiStorage = createApiStorage();
        apiStorage.setPHONE_NUMBER(phone);
        TelegramClient client = Kotlogram.getDefaultClient(Utils.application, apiStorage);
        try {
//            if (apiStorage.loadAuthKey() == null) {
//
//                    authorization(client, apiStorage);
//            }
            stringBuilder = getMessages(client);
//            client.authLogOut();
//            apiStorage.deleteAuthKey();
        } catch (RpcErrorException | IOException e) {
            e.printStackTrace();
        } finally {
            client.close(false);
        }
        return stringBuilder;
    }

    private StringBuilder getMessages(TelegramClient client) throws RpcErrorException, IOException {
        int count = 10;
        StringBuilder stringBuilder = new StringBuilder();
        TLAbsDialogs tlAbsDialogs = client.messagesGetDialogs(false,0, 0, new TLInputPeerEmpty(), 1);
        TLAbsInputPeer inputPeer = getInputPeer(tlAbsDialogs);

        TLAbsMessages tlAbsMessages = client.messagesGetHistory(inputPeer, 0, 0, 10, count, 0, 0);
        tlAbsMessages.getMessages().forEach(message -> {
            if (message instanceof TLMessage)
                stringBuilder.append(((TLMessage) message).getMessage() + " *** ");
            else
                stringBuilder.append("Service message " + " *** ");
        });
        return stringBuilder;
    }

    private HashMap<Integer, String> createNameMap(TLAbsDialogs tlAbsDialogs) {
        // Map peer id to name
        HashMap<Integer, String> nameMap = new HashMap<>();

        tlAbsDialogs.getUsers().stream()
                .map(TLAbsUser::getAsUser)
                .forEach(user -> nameMap.put(user.getId(),
                        user.getFirstName() + " " + user.getLastName()));

        tlAbsDialogs.getChats().stream()
                .forEach(chat -> {
                    if (chat instanceof TLChannel) {
                        nameMap.put(chat.getId(), ((TLChannel) chat).getTitle());
                    } else if (chat instanceof TLChannelForbidden) {
                        nameMap.put(chat.getId(), ((TLChannelForbidden) chat).getTitle());
                    } else if (chat instanceof TLChat) {
                        nameMap.put(chat.getId(), ((TLChat) chat).getTitle());
                    } else if (chat instanceof TLChatEmpty) {
                        nameMap.put(chat.getId(), "Empty chat");
                    } else if (chat instanceof TLChatForbidden) {
                        nameMap.put(chat.getId(), ((TLChatForbidden) chat).getTitle());
                    }
                });

        return nameMap;
    }
}
