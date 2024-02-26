package org.example;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Properties;

public class Main {
    public static void main (String[] args) {
        String email = "nexitestautomation@gmail.com";
        String password = "ntgq zkne mhri vplj";
        final String username = email;
        final String host = "imap.gmail.com";
        String targetPhrase = "Attenzione! Non è stato possibile richiamare le informazioni sul luogo di accesso.";
        boolean phraseExists = false;
        int unreadMessages = 0;

        Properties properties = new Properties();
        properties.put("mail.imap.host", host);
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        properties.setProperty("mail.imaps.auth", "true");
        properties.setProperty("mail.imap.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties);
        try {
            //connessione server imap
            Store store = session.getStore("imap");
            store.connect(host, username, password);
            //creazione folder object
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            Message lastMessage = messages[messages.length - 1];
            for (Message message : messages){
                Flags flags = message.getFlags();
                if(!flags.contains(Flags.Flag.SEEN)){
                    unreadMessages ++;
                    System.out.println("messages.length---" + messages.length);
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("From: " + message.getFrom()[0]);
                    //latestMessage.setFlag(Flags.Flag.SEEN, true); //Segna il messaggio come leto
                    Object content = message.getContent();
                    if (content instanceof MimeMultipart) {
                        MimeMultipart multipart = (MimeMultipart) content;
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart bodyPart = multipart.getBodyPart(i);
                            if (bodyPart.isMimeType("multipart/related")) {
                                // Handle related multipart
                                MimeMultipart relatedMultipart = (MimeMultipart) bodyPart.getContent();
                                for (int j = 0; j < relatedMultipart.getCount(); j++) {
                                    BodyPart relatedBodyPart = relatedMultipart.getBodyPart(j);
                                    if (relatedBodyPart.isMimeType("text/html")) {
                                        // Print HTML content
                                        String htmlContent = (String) relatedBodyPart.getContent();
                                        //System.out.println("HTML Content:");
                                        //System.out.println(relatedBodyPart.getContent());
                                        if(htmlContent.contains(targetPhrase)){
                                            System.out.println("Frase trovata");
                                            message.setFlag(Flags.Flag.SEEN, true);
                                            phraseExists = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (content instanceof String) {
                        if(((String) content).contains(targetPhrase)){
                            message.setFlag(Flags.Flag.SEEN, true);
                            System.out.println("Phrase found !");
                            phraseExists = true;
                        }
                    }
                    if(phraseExists == false){
                        System.out.println("Frase non esiste");
                    }
                }
            }
            if(unreadMessages == 0){
                System.out.println("0 NEW email found in INBOX :( ");
            }else{
                System.out.println(unreadMessages+ " NEW email found unread in INBOX ");
            }

            folder.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}