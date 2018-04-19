package com.dummy.myerp.business.contrat.manager;

import java.util.List;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SoldeCompteComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.technical.exception.TechnicalException;


/**
 * Interface du manager du package comptabilite.
 */
public interface ComptabiliteManager {

    /**
     * Renvoie la liste des comptes comptables.
     *
     * @return {@link List}
     */
    List<CompteComptable> getListCompteComptable();


    /**
     * Renvoie la liste des journaux comptables.
     *
     * @return {@link List}
     */
    List<JournalComptable> getListJournalComptable();


    /**
     * Renvoie la derniere valeur d'une séquence de journal comptable
     *
     * @return {@link List}
     */
    int getValueJournalComptable(String journalCode, int year) throws NotFoundException;

    /**
     * met à jour la derniere valeur d'une séquence de journal comptable
     *
     * @return {@link List}
     */
    void updateValueJournalComptable(String journalCode, int year, int valeur) throws NotFoundException;

    /**
     * verifie l'existence du code d'un journal comptable
     *
     * @return {@link List}
     */
    boolean isJournalComptableExiste(String journalCode);


    /**
     * Renvoie la liste des écritures comptables.
     *
     * @return {@link List}
     */
    List<EcritureComptable> getListEcritureComptable();

    /**
     * Renvoie la liste des écritures comptables sur une periode spécifiée.
     *
     * @return {@link List}
     */
    List<EcritureComptable> getListEcritureComptable(String dateFromWhenEngFormat, String dateToWhenEngFormat) throws NotFoundException;

    /**
     * Ajoute une référence à l'écriture comptable.
     *
     * <strong>RG_Compta_5 : </strong>
     * La référence d'une écriture comptable est composée du code du journal dans lequel figure l'écriture
     * suivi de l'année et d'un numéro de séquence (propre à chaque journal) sur 5 chiffres incrémenté automatiquement
     * à chaque écriture. Le formatage de la référence est : XX-AAAA/#####.
     * <br>
     * Ex : Journal de banque (BQ), écriture au 31/12/2016
     * <pre>BQ-2016/00001</pre>
     *
     * <p><strong>Attention :</strong> l'écriture n'est pas enregistrée en persistance</p>
     * @param pEcritureComptable L'écriture comptable concernée
     */
    void addReference(EcritureComptable pEcritureComptable) throws FunctionalException;

    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion.
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException;

    /**
     * Insert une nouvelle écriture comptable.
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException;

    /**
     * Met à jour l'écriture comptable.
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException;

    /**
     * Supprime l'écriture comptable d'id {@code pId}.
     *
     * @param pId l'id de l'écriture
     */
    void deleteEcritureComptable(Integer pId);

    /**
     * Récupère et calcul le solde d'un compte comptable en se basant sur l'ensemble des écritures comptables de la base.
     *
     * @param numeroCompteComptableInput numéro du compte comptable
     */
    SoldeCompteComptable findSoldeCompteComptable(int numeroCompteComptableInput);

    /**
     * Récupère et calcul le solde d'un compte comptable en se basant sur les écritures comptables dont la date est comprise entre dateFromWhen et dateToWhen.
     *
     * @param numeroCompteComptableInput numéro du compte comptable
     * @param dateFromWhen date de début à prendre en compte pour faire le solde
     * @param dateToWhen date de fin à prendre en compte pour faire le solde
     */
    SoldeCompteComptable findSoldeCompteComptable(int numeroCompteComptableInput, String dateFromWhen, String dateToWhen) throws TechnicalException, NotFoundException;

}
