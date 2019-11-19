package com.hedera.cli.hedera.crypto;

import com.hedera.cli.shell.ShellHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ValidateTransferListTest {

    @InjectMocks
    private ValidateTransferList validateTransferList;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private ValidateAmount validateAmount;

    @Mock
    private ValidateAccounts validateAccounts;

    private CryptoTransferOptions cryptoTransferOptions;
    private CryptoTransferOptions.Exclusive exclusive;
    private CryptoTransferOptions.Dependent dependent;

    @Test
    public void assertAutowiredDependenciesNotNull() {
        validateTransferList.setShellHelper(shellHelper);
        assertNotNull(validateTransferList.getShellHelper());

        validateTransferList.setValidateAccounts(validateAccounts);
        assertNotNull(validateTransferList.getValidateAccounts());

        validateTransferList.setValidateAmount(validateAmount);
        assertNotNull(validateTransferList.getValidateAmount());

        assertNotNull(validateTransferList);
    }

    @Test
    public void sumOfAmountInTiny() {

        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("400,1000,10030");
        exclusive.setTransferListAmtHBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateTransferList.setCryptoTransferOptions(cryptoTransferOptions);
        assertEquals(cryptoTransferOptions, validateTransferList.getCryptoTransferOptions());
        validateTransferList.setTiny(true);
        List<String> amountList = new ArrayList<>();
        amountList.add("50");
        amountList.add("50");
        validateTransferList.setAmountList(amountList);
        when(validateAmount.sumOfTinybarsInLong(amountList)).thenReturn(100L);
        assertEquals(100L, validateTransferList.sumOfAmountList());
    }

    @Test
    public void sumOfAmountInHbar() {

        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("");
        exclusive.setTransferListAmtHBars("0.1,0.2,0.3");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateTransferList.setCryptoTransferOptions(cryptoTransferOptions);

        validateTransferList.setTiny(false);
        List<String> amountList = new ArrayList<>();
        amountList.add("0.50");
        amountList.add("0.40");
        validateTransferList.setAmountList(amountList);
        when(validateAmount.sumOfHbarsInLong(amountList)).thenReturn(90000000L);
        assertEquals(90000000L, validateTransferList.sumOfAmountList());
    }

    @Test
    public void updateAmountListTinybar() {
        dependent = new CryptoTransferOptions.Dependent();

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("500000,400000");
        exclusive.setTransferListAmtHBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateTransferList.setCryptoTransferOptions(cryptoTransferOptions);

        List<String> amountList = new ArrayList<>();
        amountList.add("500000");
        amountList.add("400000");
        validateTransferList.setAmountList(amountList);
        validateTransferList.setTiny(true);
        assertTrue(validateTransferList.isTiny());
        long sumOfRecipientAmount = 900000L;
        validateTransferList.updateAmountList(sumOfRecipientAmount);
        amountList.add(0,"-900000");
        assertEquals(amountList, validateTransferList.getFinalAmountList(cryptoTransferOptions));
    }

    @Test
    public void convertAmountListToTinybar() {
        List<String> amountList = new ArrayList<>();
        amountList.add("0.006");
        amountList.add("0.003");
        validateTransferList.setAmountList(amountList);
        validateTransferList.setTiny(false);
        long sumOfReceipientsAmount = 900000L;
        validateTransferList.finalAmountList(amountList, sumOfReceipientsAmount);
        verify(validateAmount, times(2)).convertHbarToLong(any());
    }

    @Test
    public void verifyAmountListCase3Senders() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        dependent.setSenderList("0.0.1001,0.0.1002,0.0.1005");
        dependent.setRecipientList("0.0.1003,0.0.1004");

        exclusive = new CryptoTransferOptions.Exclusive();
        exclusive.setTransferListAmtTinyBars("500000,400000");
        exclusive.setTransferListAmtHBars("");

        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransferOptions.setExclusive(exclusive);
        validateTransferList.setCryptoTransferOptions(cryptoTransferOptions);
        List<String> amountList = Arrays.asList(exclusive.getTransferListAmtTinyBars().split(","));
        System.out.println(amountList);
        validateTransferList.setAmountList(amountList);
        validateTransferList.setTiny(false);

        validateTransferList.setSenderList(Arrays.asList(dependent.getSenderList().split(",")));
        validateTransferList.verifyAmountList(cryptoTransferOptions);
        verify(validateAmount, times(1)).getAmountList(cryptoTransferOptions);
        verify(validateAccounts,times(1)).getSenderList(cryptoTransferOptions);
        verify(validateAccounts, times(1)).getRecipientList(cryptoTransferOptions);
        verify(validateAmount, times(1)).isTiny(cryptoTransferOptions);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).printWarning(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "More than 2 senders not supported";
        assertEquals(expected, actual);
    }
}
