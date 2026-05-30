package org.harold.plateforme.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String ressource;
    private final String champ;
    private final Object valeur;

    public ResourceNotFoundException(String ressource, String champ, Object valeur) {
        super(String.format("%s non trouvé(e) avec %s : '%s'", ressource, champ, valeur));
        this.ressource = ressource;
        this.champ = champ;
        this.valeur = valeur;
    }

    public String getRessource() { return ressource; }
    public String getChamp() { return champ; }
    public Object getValeur() { return valeur; }
}