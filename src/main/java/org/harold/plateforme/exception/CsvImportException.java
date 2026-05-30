package org.harold.plateforme.exception;

import java.util.List;


//Ce que j'ai fait ici :
//Deux constructeurs : un avec une liste d'erreurs détaillées et un sans
//erreurs contient le détail, ligne par ligne des problèmes rencontrés lors de l'import
public class CsvImportException extends RuntimeException {

    private final List<String> erreurs;

    public CsvImportException(String message, List<String> erreurs) {
        super(message);
        this.erreurs = erreurs;
    }

    public CsvImportException(String message) {
        super(message);
        this.erreurs = List.of();
    }

    public List<String> getErreurs() { return erreurs; }
}