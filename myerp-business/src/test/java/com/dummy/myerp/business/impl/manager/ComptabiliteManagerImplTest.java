package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.technical.exception.TechnicalException;
import com.dummy.myerp.testbusiness.business.SpringRegistry;
import org.junit.Test;
import com.dummy.myerp.technical.exception.FunctionalException;

import static org.junit.Assert.*;


public class ComptabiliteManagerImplTest {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();


    @Test
    public void checkEcritureComptableUnit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference("AC-2018/00001");
        vEcritureComptable.setLibelle("Libelle");
        //
        BigDecimal credit1=new BigDecimal(123);
        BigDecimal debit1=new BigDecimal(123);
        //
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, debit1.setScale(2,RoundingMode.HALF_UP),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null, credit1.setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    //
    //test le controle de validation des champs de EcritureComptable par le ConstraintValidator
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    //
    //test l'équilibre de l'écriture comptable
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        //setjournal
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        //setref
        vEcritureComptable.setReference("AC-2016/00001");
        //setdate
        Date dateEcritureComptable =new Date();
        dateEcritureComptable.setYear(116);//116 aka 2016-1900(Reference pour l'objet Date)
        dateEcritureComptable.setMonth(3);
        dateEcritureComptable.setDate(22);
        vEcritureComptable.setDate(dateEcritureComptable);
        //setlibelle
        vEcritureComptable.setLibelle("Libelle");
        //setlignesecritures
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    //
    //test de séparation des opérations de débit et de crédit
    //Si moins de deux lignes -> catché par le ConstraintValidator
    //la contrainte RG3 explicite des deux lignes minimum est donc innutile
    //test avec une ligne équilibrée artificiellement en une seule ligne
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        //setjournal
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        //setref
        vEcritureComptable.setReference("AC-2016/00001");
        //setdate
        Date dateEcritureComptable =new Date();
        dateEcritureComptable.setYear(116);//116 aka 2016-1900(Reference pour l'objet Date)
        dateEcritureComptable.setMonth(3);
        dateEcritureComptable.setDate(22);
        vEcritureComptable.setDate(dateEcritureComptable);
        //setlibelle
        vEcritureComptable.setLibelle("Libelle");
        //setlignesecritures
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),new BigDecimal(123).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    //
    //test de séparation des opérations de débit et de crédit
    //Si moins de deux lignes -> catché par le ConstraintValidator
    //la contrainte RG3 explicite des deux lignes minimum est donc innutile
    //test avec une ligne équilibrée en valeur null
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3_BIS() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        //setjournal
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        //setref
        vEcritureComptable.setReference("AC-2016/00001");
        //setdate
        Date dateEcritureComptable =new Date();
        dateEcritureComptable.setYear(116);//116 aka 2016-1900(Reference pour l'objet Date)
        dateEcritureComptable.setMonth(3);
        dateEcritureComptable.setDate(22);
        vEcritureComptable.setDate(dateEcritureComptable);
        //setlibelle
        vEcritureComptable.setLibelle("Libelle");
        //setlignesecritures
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),null, null,null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    //
    //test de validité des valeurs négatives
    @Test
    public void checkEcritureComptableUnitRG4() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        //setjournal
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        //setref
        vEcritureComptable.setReference("AC-2016/00001");
        //setdate
        Date dateEcritureComptable =new Date();
        dateEcritureComptable.setYear(116);//116 aka 2016-1900(Reference pour l'objet Date)
        dateEcritureComptable.setMonth(3);
        dateEcritureComptable.setDate(22);
        vEcritureComptable.setDate(dateEcritureComptable);
        //setlibelle
        vEcritureComptable.setLibelle("Libelle");
        //setlignesecritures
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(-123).setScale(2,RoundingMode.HALF_UP),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(-123).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    //
    //test de validité de l'indice de réference
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        //setjournal
        vEcritureComptable.setJournal(new JournalComptable("AS", "Achat"));
        //setref
        vEcritureComptable.setReference("AC-2016/00001");
        //setdate
        Date dateEcritureComptable =new Date();
        dateEcritureComptable.setYear(116);//116 aka 2016-1900(Reference pour l'objet Date)
        dateEcritureComptable.setMonth(3);
        dateEcritureComptable.setDate(22);
        vEcritureComptable.setDate(dateEcritureComptable);
        //setlibelle
        vEcritureComptable.setLibelle("Libelle");
        //setlignesecritures
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123).setScale(2,RoundingMode.HALF_UP)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }


    //test de validité du format numérique des lignes d'écritures comptables
    @Test
    public void checkEcritureComptableContextRG7 () throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'anomalies dans le format
        ectCptble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123).setScale(2,RoundingMode.HALF_UP)));
        ectCptble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, new BigDecimal(123).setScale(2,RoundingMode.HALF_UP), null));
        manager.checkEcritureComptableUnit(ectCptble);
    }


    //test d'invalidité du format numérique des lignes d'écritures comptables
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG7_BIS () throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'anomalies dans le format
        ectCptble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null, new BigDecimal(123.785)));
        ectCptble.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, new BigDecimal(123), null));
        manager.checkEcritureComptableUnit(ectCptble);
    }


    //
    //test calcul de solde par compte comptable en cherchant dans toute la base
    @Test
    public void checkEcritureComptableUnitRG1() throws Exception {
        // Initialisation Spring
        SpringRegistry.init();
        //
        // Test avec numeroCompteComptable innexistant renvoi solde == 0
        SoldeCompteComptable solde0;
        solde0=manager.findSoldeCompteComptable(0);
        // Le solde doit être == 0
        assertTrue(solde0.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
        //
        // Test avec de vraies valeurs renvoi solde != 0
        SoldeCompteComptable solde411;
        solde411=manager.findSoldeCompteComptable(411);
        assertFalse(solde411.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
    }

    //
    //test calcul de solde par compte comptable en cherchant sur une période précise
    @Test
    public void checkEcritureComptableUnitRG1_BIS() throws Exception {
        // Initialisation Spring
        SpringRegistry.init();
        //
        // Test avec numeroCompteComptable innexistant renvoi solde == 0 et date aboutissant à des résultats
        SoldeCompteComptable solde0;
        solde0=manager.findSoldeCompteComptable(0,"2015-01-01", "2017-01-01");
        // Le solde doit être == 0
        assertTrue(solde0.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
        //
        // Test avec de vraies valeurs renvoi solde != 0
        SoldeCompteComptable solde411;
        solde411=manager.findSoldeCompteComptable(411,"2015-01-01", "2017-01-01");
        assertFalse(solde411.getAbsSolde().compareTo(new BigDecimal("0").setScale(2,RoundingMode.HALF_UP)) == 0);
    }

    //test calcul de solde par compte comptable en cherchant sur une période avec aucun résultat
    @Test(expected = NotFoundException.class)
    public void checkEcritureComptableUnitRG1_TER() throws Exception {
        // Initialisation Spring
        SpringRegistry.init();
        //
        // Test avec de vraies valeurs renvoi solde != 0
        SoldeCompteComptable solde411;
        solde411=manager.findSoldeCompteComptable(411,"2000-01-01", "2001-01-01");
    }

    //test calcul de solde par compte comptable en cherchant avec date invalide
    @Test(expected = TechnicalException.class)
    public void checkEcritureComptableUnitRG1_QUATER() throws Exception {
        // Initialisation Spring
        SpringRegistry.init();
        //
        // Test avec de vraies valeurs renvoi solde != 0
        SoldeCompteComptable solde411;
        solde411=manager.findSoldeCompteComptable(411,"3dzuh|de", "2018-01-01");
    }


    //test unicité de la reference
    @Test
    public void checkEcritureComptableContextRG6() throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        ectCptble.setId(-6);
        ectCptble.setReference(ectCptble.getReference().replace("2016","2017"));
        //execution du test
        manager.checkEcritureComptableContext(ectCptble);
        //modifs pour création d'une nouvelle réference unique
        ectCptble.setId(null);
        ectCptble.setReference(ectCptble.getReference().replace("2016","2017"));
        //execution du test
        manager.checkEcritureComptableContext(ectCptble);
    }


    //test non-unicité de la reference à id <>
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6_BIS() throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        ectCptble.setId(-6);
        manager.checkEcritureComptableContext(ectCptble);
    }


    //test non-unicité de la reference à id null
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6_TER() throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        ectCptble.setId(null);
        manager.checkEcritureComptableContext(ectCptble);
    }


    //test non-unicité de la reference à id existant <>
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6_QUATER() throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        ectCptble.setId(-4);
        manager.checkEcritureComptableContext(ectCptble);
    }


    //test unicité/non-unicité, la réference existe mais l'id est =
    @Test
    public void checkEcritureComptableContextRG6_QUINQUIES () throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        //modifs pour création d'une réference unique
        manager.checkEcritureComptableContext(ectCptble);
    }

    //test général des écritures comptables
    @Test
    public void checkEcritureComptableGeneral() throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(2);// On choisit une écriture comptable
        //
        manager.checkEcritureComptable(ectCptble);// On teste l'écriture comptable sur toutes les règles
    }


    //test addreference sans erreur
    @Test
    public void addreferenceTest() throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        // suppression la reference
        ectCptble.setReference("");
        //
        manager.addReference(ectCptble);
        //
    }


    //test addreference test journal_code incorrect
    @Test(expected = FunctionalException.class)
    public void addreferenceTest_BIS() throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        // suppression la reference et set d'un journal_code erroné
        ectCptble.setReference("");
        JournalComptable jrnl = new JournalComptable("AS","AS de pique");
        ectCptble.setJournal(jrnl);
        // Test de deux cas d'exceptions fonctionnelles
        // 1) journal_code non reconnu
        // 2) journal_code nul
        try{
            manager.addReference(ectCptble);
        } catch(FunctionalException e) {
            JournalComptable jrnl2 = new JournalComptable();
            ectCptble.setJournal(jrnl2);
            manager.addReference(ectCptble);
        }
        //
    }

    // test addreference sans erreur avec commencement d'une nouvelle numerotation
    // suite à un changement d'année (premiere écriture comptable de l'année)
    @Test
    public void addreferenceTest_TER() throws Exception {
        SpringRegistry.init();
        //def
        EcritureComptable ectCptble;
        List<EcritureComptable> listEcritures;
        //gets
        listEcritures = manager.getListEcritureComptable();
        ectCptble=listEcritures.get(0);
        // suppression la reference et modification de l'année
        ectCptble.setReference("");
        Date newDate = new Date();
        newDate.setYear(117); // + 1900
        newDate.setMonth(0); // Janv
        newDate.setDate(1); // 1er
        ectCptble.setDate(newDate);
        //
        manager.addReference(ectCptble);
        //
        String indiceJournal = ectCptble.getReference().substring(ectCptble.getReference().length() - 5, ectCptble.getReference().length());
        //
        assertTrue(indiceJournal.equals("00001"));
    }

    //test général avec génération de réferences, des checks, des insert, des select, des updates et des delete via un petit scénario
    @Test
    public void checkSenarioEcritureComptable() throws Exception {
        SpringRegistry.init();
        //def n' init
        EcritureComptable vEcritureComptable;
        EcritureComptable vEcritureComptable2;
        List<EcritureComptable> lEcritures;
        int idEcritureEnBase=0;
        boolean isPushed=false;
        boolean isUpdated=false;
        boolean isDeleted=false;
        boolean testSucceed=false;
        // set journal comptable associé
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("VE", "Vente"));
        //vsetDate
        Date dateEcritureComptable = new Date();
        dateEcritureComptable.setYear(118);//118 aka 2018-1900(Reference pour l'objet Date)
        dateEcritureComptable.setMonth(0);
        dateEcritureComptable.setDate(1);
        vEcritureComptable.setDate(dateEcritureComptable);
        //
        // definition de la réference associée
        manager.addReference(vEcritureComptable);
        //
        vEcritureComptable.setLibelle("TMA Appli Zzz");
        //
        BigDecimal ttc=new BigDecimal(5000);
        BigDecimal ht=new BigDecimal(4166.66);
        BigDecimal tva=new BigDecimal(833.34);
        //
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Facture C110005", ttc.setScale(2,RoundingMode.HALF_UP),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(706),
                "TMA Appli Zzz", null, ht.setScale(2,RoundingMode.HALF_UP)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4457),
                "TVA 20%", null, tva.setScale(2,RoundingMode.HALF_UP)));
        //
        vEcritureComptable2 = (EcritureComptable)vEcritureComptable.clone();
        //
        //
        manager.checkEcritureComptable(vEcritureComptable);// On teste l'écriture comptable sur toutes les règles
        // push
        manager.insertEcritureComptable(vEcritureComptable);// Test de l'écriture en base
        // pull et check
        lEcritures = manager.getListEcritureComptable();
        for (EcritureComptable ecr:lEcritures) {
            if(ecr.getReference().equals(vEcritureComptable.getReference())){
                idEcritureEnBase = ecr.getId();
                isPushed = true;
            }
        }
        // modifs
        vEcritureComptable2.setId(idEcritureEnBase);
        vEcritureComptable2.setDate(new Date());
        //
        // check
        manager.checkEcritureComptable(vEcritureComptable2);// On teste l'écriture comptable sur toutes les règles
        // update
        manager.updateEcritureComptable(vEcritureComptable2);// Test de l'écriture en base
        //
        // pull et check
        lEcritures = manager.getListEcritureComptable();
        for (EcritureComptable ecr:lEcritures) {
            if(ecr.getReference().equals(vEcritureComptable.getReference()) && ecr.getDate().getDate() == vEcritureComptable2.getDate().getDate() && ecr.getDate().getMonth() == vEcritureComptable2.getDate().getMonth() && ecr.getDate().getYear() == vEcritureComptable2.getDate().getYear()){
                idEcritureEnBase=ecr.getId();
                isUpdated=true;
            }
        }
        // delete
        try {
            manager.deleteEcritureComptable(idEcritureEnBase);// Test de suppression en base
            isDeleted = true;
        }catch(Exception e){
            // problem here
            e.printStackTrace();
        }
        testSucceed = isPushed && isUpdated && isDeleted;
        //
        assertTrue(testSucceed);
    }
}
