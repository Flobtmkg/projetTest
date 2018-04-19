package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;


public class EcritureComptableTest {

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit).setScale(2, RoundingMode.HALF_UP);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit).setScale(2, RoundingMode.HALF_UP);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero), vLibelle, vDebit, vCredit);
        return vRetour;
    }

    @Test
    public void isEquilibreeTest() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());

        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }

    // test les methodes de somme des crédit et des débits
    @Test
    public void TotauxTest() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();
        //
        String debit1 = "200.50";
        String debit2 = "100.50";
        String debit3 = null;
        String debit4 = "40";
        String credit1 = null;
        String credit2 = "33";
        String credit3 = "301";
        String credit4 = "7";
        //
        vEcriture.setLibelle("Sommes totales");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, debit1, credit1));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, debit2, credit2));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, debit3, credit3));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, debit4, credit4));
        // On traite manuellement les variables null en 0
        // pour faciliter la somme de comparaison et verifier que les null sont correctement traités dans getTotalCredit et getTotalDebit
        debit3="0";
        credit1="0";
        //
        BigDecimal sommeDebit=new BigDecimal(debit1).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(debit2).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(debit3).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(debit4).setScale(2,RoundingMode.HALF_UP))));
        BigDecimal sommeCredit=new BigDecimal(credit1).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(credit2).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(credit3).setScale(2,RoundingMode.HALF_UP).add(new BigDecimal(credit4).setScale(2,RoundingMode.HALF_UP))));
        //
        boolean isDebitOk=false;
        boolean isCreditOk=false;
        //
        if(sommeDebit.compareTo(vEcriture.getTotalDebit())==0){
            isDebitOk=true;
        }
        if(sommeCredit.compareTo(vEcriture.getTotalCredit())==0){
            isCreditOk=true;
        }
        //
        Assert.assertTrue(isDebitOk && isCreditOk);
    }

}
