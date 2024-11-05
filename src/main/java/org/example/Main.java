package org.example;

import java.util.*;


interface DocumentInterface {
    String getContent(User user) throws AccessDeniedException;
}


class Document implements DocumentInterface {
    private String content;

    public Document(String content) {
        this.content = content;
    }

    @Override
    public String getContent(User user) {
        return content;
    }
}


class DocumentProxy implements DocumentInterface {
    private Document realDocument;
    private AccessControlService accessControlService;

    public DocumentProxy(Document realDocument) {
        this.realDocument = realDocument;
        this.accessControlService = AccessControlService.getInstance();
    }

    @Override
    public String getContent(User user) throws AccessDeniedException {
        if (accessControlService.isAllowed(user.getUsername())) {
            return realDocument.getContent(user);
        } else {
            throw new AccessDeniedException("Access denied for user " + user.getUsername());
        }
    }
}


class AccessControlService {
    private static AccessControlService instance;
    private Set<String> allowedUsers;

    private AccessControlService() {
        allowedUsers = new HashSet<>();
    }

    public static AccessControlService getInstance() {
        if (instance == null) {
            instance = new AccessControlService();
        }
        return instance;
    }

    public void allowUser(String username) {
        allowedUsers.add(username);
    }

    public boolean isAllowed(String username) {
        return allowedUsers.contains(username);
    }
}


class User {
    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}


class AccessDeniedException extends Exception {
    public AccessDeniedException(String message) {
        super(message);
    }
}


public class Main {
    public static void main(String[] args) {

        AccessControlService accessControlService = AccessControlService.getInstance();
        accessControlService.allowUser("sara");

        DocumentInterface unprotectedDoc = new Document("This is an unprotected document.");
        DocumentInterface protectedDoc = new DocumentProxy(new Document("This is a protected document."));


        User alice = new User("sara");
        User bob = new User("sami");

        try {

            System.out.println("Unprotected Document (sara): " + unprotectedDoc.getContent(alice));

            System.out.println("Protected Document (sara): " + protectedDoc.getContent(alice));


            System.out.println("Protected Document (sami): " + protectedDoc.getContent(bob));
        } catch (AccessDeniedException e) {
            System.out.println(e.getMessage());
        }
    }
}
