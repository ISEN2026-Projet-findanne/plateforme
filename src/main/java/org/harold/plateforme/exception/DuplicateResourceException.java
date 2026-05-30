package org.harold.plateforme.exception;

/**
 * Exception levée lorsqu'on tente de créer une ressource qui existe déjà.
 *
 * <p>Exemples : email déjà utilisé, numéro étudiant déjà pris,
 * classe déjà attribuée à une promotion pour une année académique.</p>
 *
 * @author Harold
 * @version 1.0
 */
public class DuplicateResourceException extends RuntimeException {

    private final String ressource;
    private final String champ;
    private final Object valeur;

    /**
     * Crée une exception avec le détail de la ressource dupliquée.
     *
     * @param ressource     le nom de la ressource concernée (ex: "Etudiant")
     * @param champ         le champ en doublon (ex: "numeroEtudiant")
     * @param valeur        la valeur en doublon (ex: "ETU2024001")
     */
    public DuplicateResourceException(String ressource, String champ, Object valeur) {
        super(String.format("%s existe déjà avec %s : '%s'", ressource, champ, valeur));
        this.ressource = ressource;
        this.champ = champ;
        this.valeur = valeur;
    }

    public String getRessource() { return ressource; }
    public String getChamp() { return champ; }
    public Object getValeur() { return valeur; }
}