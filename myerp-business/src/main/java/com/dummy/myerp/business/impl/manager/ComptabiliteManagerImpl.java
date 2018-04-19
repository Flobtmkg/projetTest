package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.TechnicalException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    @Override
    public int getValueJournalComptable(String journalCode, int year) throws NotFoundException{
        return getDaoProxy().getComptabiliteDao().getValeurJournalComptableByFeatures(journalCode, year);
    }

    @Override
    public void updateValueJournalComptable(String journalCode, int year, int valeur) throws NotFoundException {
        getDaoProxy().getComptabiliteDao().updateSequenceJournalComptable(journalCode, year, valeur);
    }

    @Override
    public boolean isJournalComptableExiste(String journalCode) {
        return getDaoProxy().getComptabiliteDao().isJournalCodeExiste(journalCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable(String dateFromWhenEngFormat, String dateToWhenEngFormat) throws NotFoundException{
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable(dateFromWhenEngFormat, dateToWhenEngFormat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) throws FunctionalException {
        //
        String ValueJC;
        String fiveZero = "00000";
        String jcCode = pEcritureComptable.getJournal().getCode();
        // Vérification de l'existance du code journal
        if(!isJournalComptableExiste(jcCode)){
            throw new FunctionalException("Le code journal de l'écriture comptable n'est pas reconu");
        }
        //
        int year = pEcritureComptable.getDate().getYear()+1900;
        try{
            ValueJC = String.valueOf(getValueJournalComptable(jcCode, year)+1);
        } catch (NotFoundException e){
            // pas de séquence de journal pour ce code comptable et cette année
            ValueJC = "1";
        }
        // formalisation de ValueJC pour l'integration dans la reference (5 caractères)
        ValueJC = fiveZero.substring(0, 5 - ValueJC.length()) + ValueJC;
        String reference;
        reference = jcCode + "-" + year + "/" + ValueJC;
        // intégration de la réference à l'écriture comptable
        pEcritureComptable.setReference(reference);
        //
        try {
            updateValueJournalComptable(jcCode, year, Integer.parseInt(ValueJC));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }
        // ===== RG_Compta_3 : innutile parce que géré par le ConstraintValidator
        //
        // ===== RG_Compta_5 : vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
        if(pEcritureComptable.getReference().substring(0,2).equals(pEcritureComptable.getJournal().getCode()) == false){
            //ERREUR code journal
            throw new FunctionalException("Le format ou le contenu du code journal de la reference de l'écriture comptable reçu : " + pEcritureComptable.getReference().substring(0,2) + " ne correspond pas à ce qui est attendu : " + pEcritureComptable.getJournal().getCode() + ".");
        }
        if(pEcritureComptable.getReference().substring(3,7).equals(String.valueOf(pEcritureComptable.getDate().getYear()+1900))==false){
            //ERREUR L'année
            throw new FunctionalException("Le format ou le contenu de l'année de la reference de l'écriture comptable reçu : " + pEcritureComptable.getReference().substring(3,7) + " ne correspond pas à ce qui est attendu : " + String.valueOf(pEcritureComptable.getDate().getYear()+1900) + ".");
        }
        // ===== RG_Compta_7 : Deux chiffres après la virgule pour chaque ligne d'écriture comptable
        List<LigneEcritureComptable> listLigne = pEcritureComptable.getListLigneEcriture();
        for (LigneEcritureComptable li:listLigne) {
            if(li.getDebit() != null && li.getDebit().scale() > 2){
                throw new FunctionalException("Le format des valeurs numériques d'au moins une ligne d'écriture ne correspond pas au format attendu (deux chiffres après la virgule), l'écriture comptable n'est pas valide");
            }
            if(li.getCredit() != null && li.getCredit().scale() > 2){
                throw new FunctionalException("Le format des valeurs numériques d'au moins une ligne d'écriture ne correspond pas au format attendu (deux chiffres après la virgule), l'écriture comptable n'est pas valide");
            }
        }

    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        //
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(pEcritureComptable.getReference());
                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null || pEcritureComptable.getId().intValue() != vECRef.getId().intValue()) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    // ===== RG_Compta_1 : Il est possible d'obtenir le solde par compte comptable (sur une periode donnée)
    public SoldeCompteComptable findSoldeCompteComptable(int numeroCompteComptableInput, String dateFromWhen, String dateToWhen) throws TechnicalException, NotFoundException {
        //
        //Test des paramètres de Date
        try {
            LocalDate dateTester = LocalDate.parse(dateFromWhen);
            LocalDate dateTester2 = LocalDate.parse(dateToWhen);
        } catch (Exception e){
            throw new TechnicalException("Erreur dans le contenu ou le format des dates");
        }
        //
        // On récupère les lignes
        List<EcritureComptable> listeDesEcritures=getListEcritureComptable(dateFromWhen, dateToWhen);
        ArrayList<LigneEcritureComptable> listeDesLignes= new ArrayList();
        //
        // On prépare la somme des débits et des crédits
        BigDecimal sommeCredit = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
        BigDecimal sommeDebit = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
        //
        // On get toutes les lignes d'écriture dans listeDesLignes
        for (EcritureComptable ec:listeDesEcritures) {
            listeDesLignes.addAll(ec.getListLigneEcriture());
        }
        //
        // On fait la somme des débits et des crédits de chaque ligne
        for (LigneEcritureComptable lec:listeDesLignes) {
            if(lec.getCompteComptable().getNumero() == numeroCompteComptableInput){
                if(lec.getCredit()!=null){
                    sommeCredit = sommeCredit.add(lec.getCredit().setScale(2,RoundingMode.HALF_UP));
                }
                if(lec.getDebit()!=null){
                    sommeDebit = sommeDebit.add(lec.getDebit().setScale(2,RoundingMode.HALF_UP));
                }
            }
        }
        // On stocke et retourne les sommes et le compteComptable associé dans un objet
        // SoldeCompteComptable qui gère le système de solde.
        SoldeCompteComptable newSoldeComptable = new SoldeCompteComptable(CompteComptable.getByNumero(getListCompteComptable(),numeroCompteComptableInput),sommeCredit,sommeDebit);
        return newSoldeComptable;
    }


    // ===== RG_Compta_1 : Il est possible d'obtenir le solde par compte comptable
    public SoldeCompteComptable findSoldeCompteComptable(int numeroCompteComptableInput) {
        //
        // On récupère les lignes
        List<EcritureComptable> listeDesEcritures=getListEcritureComptable();
        ArrayList<LigneEcritureComptable> listeDesLignes= new ArrayList();
        //
        // On prépare la somme des débits et des crédits
        BigDecimal sommeCredit = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
        BigDecimal sommeDebit = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
        //
        // On get toutes les lignes d'écriture dans listeDesLignes
        for (EcritureComptable ec:listeDesEcritures) {
            listeDesLignes.addAll(ec.getListLigneEcriture());
        }
        //
        // On fait la somme des débits et des crédits de chaque ligne
        for (LigneEcritureComptable lec:listeDesLignes) {
            if(lec.getCompteComptable().getNumero() == numeroCompteComptableInput){
                if(lec.getCredit()!=null){
                    sommeCredit = sommeCredit.add(lec.getCredit().setScale(2,RoundingMode.HALF_UP));
                }
                if(lec.getDebit()!=null){
                    sommeDebit = sommeDebit.add(lec.getDebit().setScale(2,RoundingMode.HALF_UP));
                }
            }
        }
        // On stocke et retourne les sommes et le compteComptable associé dans un objet
        // SoldeCompteComptable qui gère le système de solde.
        SoldeCompteComptable newSoldeComptable = new SoldeCompteComptable(CompteComptable.getByNumero(getListCompteComptable(),numeroCompteComptableInput),sommeCredit,sommeDebit);
        return newSoldeComptable;
    }
}
