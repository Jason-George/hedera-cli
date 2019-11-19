package com.hedera.cli.hedera.crypto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.cli.config.InputReader;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.AccountManager;
import com.hedera.cli.models.PreviewTransferList;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.shell.ShellHelper;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.CryptoTransferTransaction;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CryptoTransferTest {

    @InjectMocks
    private CryptoTransfer cryptoTransfer;

    @Mock
    private ShellHelper shellHelper;

    @Mock
    private AccountManager accountManager;

    @Mock
    private ValidateAmount validateAmount;

    @Mock
    private ValidateAccounts validateAccounts;

    @Mock
    private ValidateTransferList validateTransferList;

    @Mock
    private Hedera hedera;

    @Mock
    private InputReader inputReader;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private CryptoTransferTransaction cryptoTransferTransaction;

    private CryptoTransferOptions cryptoTransferOptions;
    private CryptoTransferOptions.Exclusive exclusive;
    private CryptoTransferOptions.Dependent dependent;
    private List<String> expectedAmountList;
    private List<String> expectedTransferList;
    private List<String> senderList;
    private List<String> recipientList;

    @BeforeEach
    public void setUp() {
        expectedAmountList = new ArrayList<>();
        expectedAmountList.add("-1400");
        expectedAmountList.add("1000");
        expectedAmountList.add("400");

        expectedTransferList = new ArrayList<>();
        expectedTransferList.add("0.0.1001");
        expectedTransferList.add("0.0.1002");
        expectedTransferList.add("0.0.1003");

        senderList = new ArrayList<>();
        senderList.add("0.0.1001");

        recipientList = new ArrayList<>();
        recipientList.add("0.0.1002");
        recipientList.add("0.0.1003");
    }

    @Test
    public void dependenciesExist() {
        cryptoTransfer.setShellHelper(shellHelper);
        assertEquals(shellHelper, cryptoTransfer.getShellHelper());
        cryptoTransfer.setAccountManager(accountManager);
        assertEquals(accountManager, cryptoTransfer.getAccountManager());
        cryptoTransfer.setHedera(hedera);
        assertEquals(hedera, cryptoTransfer.getHedera());
        cryptoTransfer.setInputReader(inputReader);
        assertEquals(inputReader, cryptoTransfer.getInputReader());
        cryptoTransfer.setTransactionManager(transactionManager);
        assertEquals(transactionManager, cryptoTransfer.getTransactionManager());
        cryptoTransfer.setO(cryptoTransferOptions);
        assertEquals(cryptoTransferOptions, cryptoTransfer.getO());
        cryptoTransfer.setValidateAccounts(validateAccounts);
        assertEquals(validateAccounts, cryptoTransfer.getValidateAccounts());
        cryptoTransfer.setValidateAmount(validateAmount);
        assertEquals(validateAmount, cryptoTransfer.getValidateAmount());
        cryptoTransfer.setValidateTransferList(validateTransferList);
        assertEquals(validateTransferList, cryptoTransfer.getValidateTransferList());
    }

    @Test
    public void isSkipPreviewTrue() {
        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(true);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);
        assertTrue(cryptoTransfer.isSkipPreview());
    }

    @Test
    public void isTinyFalse() {
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(false);
        assertFalse(cryptoTransfer.isTiny());
    }

    @Test
    public void isTinyTrue() {
        when(validateAmount.isTiny(cryptoTransferOptions)).thenReturn(true);
        assertTrue(cryptoTransfer.isTiny());
    }

    @Test
    public void addTransferList() {
        cryptoTransfer.setFinalAmountList(expectedAmountList);
        cryptoTransfer.setTransferList(expectedTransferList);
        cryptoTransfer.setCryptoTransferTransaction(cryptoTransfer.addTransferList());
        assertEquals(cryptoTransferTransaction, cryptoTransfer.getCryptoTransferTransaction());
    }

    @Test
    public void transferListToPromptPreviewMap() {
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);

        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString("0.0.1001"), "-1400");
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString("0.0.1002"), "1000");
        PreviewTransferList previewTransferList2 = new PreviewTransferList(AccountId.fromString("0.0.1003"), "400");
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);
        expectedMap.put(2, previewTransferList2);

        Map<Integer, PreviewTransferList> actualMap = cryptoTransfer.transferListToPromptPreviewMap();
        assertEquals(expectedMap.get(0).getAccountId(), actualMap.get(0).getAccountId());
        assertEquals(expectedMap.get(1).getAccountId(), actualMap.get(1).getAccountId());
        assertEquals(expectedMap.get(2).getAccountId(), actualMap.get(2).getAccountId());
    }

    @Test
    public void transferListToPromptPreviewMapIsTiny() {
        when(validateAmount.isTiny(any())).thenReturn(true);
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);

        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString("0.0.1001"), "-1400");
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString("0.0.1002"), "1000");
        PreviewTransferList previewTransferList2 = new PreviewTransferList(AccountId.fromString("0.0.1003"), "400");
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);
        expectedMap.put(2, previewTransferList2);

        Map<Integer, PreviewTransferList> actualMap = cryptoTransfer.transferListToPromptPreviewMap();
        assertEquals(expectedMap.get(0).getAccountId(), actualMap.get(0).getAccountId());
        assertEquals(expectedMap.get(1).getAccountId(), actualMap.get(1).getAccountId());
        assertEquals(expectedMap.get(2).getAccountId(), actualMap.get(2).getAccountId());
    }

    @Test
    public void promptPreviewIncorrect() throws InvalidProtocolBufferException, InterruptedException,
            TimeoutException, JsonProcessingException {

        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);

        AccountId operatorId = hedera.getOperatorId();

        when(validateAmount.isTiny(any())).thenReturn(true);
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);
        when(accountManager.promptMemoString(inputReader)).thenReturn("some memo");

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString("0.0.1001"), "-1400");
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString("0.0.1002"), "1000");
        PreviewTransferList previewTransferList2 = new PreviewTransferList(AccountId.fromString("0.0.1003"), "400");
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);
        expectedMap.put(2, previewTransferList2);
        String jsonStringTransferList = ow.writeValueAsString(expectedMap);
        String prompt = "\nOperator\n" + operatorId + "\nTransfer List\n" + jsonStringTransferList
                + "\n\nIs this correct?" + "\nyes/no";
        when(inputReader.prompt(prompt)).thenReturn("no");

        cryptoTransfer.reviewAndExecute(operatorId);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).print(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Nope, incorrect, let's make some changes";
        assertEquals(expected, actual);
    }

    @Test
    public void promptPreviewCorrectAndExecute() throws InvalidProtocolBufferException, InterruptedException,
            TimeoutException, JsonProcessingException {

        dependent = new CryptoTransferOptions.Dependent();
        dependent.setSkipPreview(false);
        cryptoTransferOptions = new CryptoTransferOptions();
        cryptoTransferOptions.setDependent(dependent);
        cryptoTransfer.setO(cryptoTransferOptions);

        AccountId operatorId = hedera.getOperatorId();

        when(validateAmount.isTiny(any())).thenReturn(true);
        when(validateAccounts.getTransferList(any())).thenReturn(expectedTransferList);
        when(validateTransferList.getFinalAmountList(any())).thenReturn(expectedAmountList);
        when(accountManager.promptMemoString(inputReader)).thenReturn("some memo");

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Map<Integer, PreviewTransferList> expectedMap = new HashMap<>();
        PreviewTransferList previewTransferList = new PreviewTransferList(AccountId.fromString("0.0.1001"), "-1400");
        PreviewTransferList previewTransferList1 = new PreviewTransferList(AccountId.fromString("0.0.1002"), "1000");
        PreviewTransferList previewTransferList2 = new PreviewTransferList(AccountId.fromString("0.0.1003"), "400");
        expectedMap.put(0, previewTransferList);
        expectedMap.put(1, previewTransferList1);
        expectedMap.put(2, previewTransferList2);
        String jsonStringTransferList = ow.writeValueAsString(expectedMap);
        String prompt = "\nOperator\n" + operatorId + "\nTransfer List\n" + jsonStringTransferList
                + "\n\nIs this correct?" + "\nyes/no";
        when(inputReader.prompt(prompt)).thenReturn("yes");

        TransactionId txid = mock(TransactionId.class);
        cryptoTransfer.setTransactionId(txid);
        CryptoTransfer cryptoTransfer1 = Mockito.spy(cryptoTransfer);
        doNothing().when(cryptoTransfer1).executeCryptoTransfer(any());

        cryptoTransfer1.reviewAndExecute(operatorId);

        ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);
        verify(shellHelper).print(valueCapture.capture());
        String actual = valueCapture.getValue();
        String expected = "Info is correct, senders will need to sign the transaction to release funds";
        assertEquals(expected, actual);

    }
}
