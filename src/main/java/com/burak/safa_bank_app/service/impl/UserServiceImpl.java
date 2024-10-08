package com.burak.safa_bank_app.service.impl;

import com.burak.safa_bank_app.config.JwtTokenProvider;
import com.burak.safa_bank_app.config.SecurityConfig;
import com.burak.safa_bank_app.dto.*;
import com.burak.safa_bank_app.entity.Role;
import com.burak.safa_bank_app.entity.User;
import com.burak.safa_bank_app.repository.UserRepository;
import com.burak.safa_bank_app.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .role(Role.ROLE_ADMIN)
                .build();

        User savedUser = userRepository.save(newUser);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Your account is successfully created! Check out latest deals. \n Your account details: \n" +
                        "Account name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() +"\n"
                + "Account number: " + savedUser.getAccountNumber())
                .build();

        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName()+ " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication =authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        EmailDetails loginAlert = EmailDetails.builder()
                .subject("LOGGED IN")
                .messageBody("Your have logged in your account. If it is not you please let us know.")
                .recipient(loginDto.getEmail())
                .build();

        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("Login Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(String.format(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, enquiryRequest.getAccountNumber()))
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(enquiryRequest.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {

        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if (!isAccountExist){
            return String.format(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, enquiryRequest.getAccountNumber());
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {

        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(String.format(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, creditDebitRequest.getAccountNumber()))
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());

        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(userToCredit);

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("DEPOSIT")
                .amount(creditDebitRequest.getAmount())
                .build();

        transactionService.saveTransaction(transactionRequest);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(userToCredit.getEmail())
                .subject("BALANCE ADDED")
                .messageBody(creditDebitRequest.getAmount() + " of credit is added to your account! Check out latest deals. \n Your account details: \n" +
                        "Account name: " + userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName() +"\n"
                        + "Account number: " + userToCredit.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseCode(String.format(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE, creditDebitRequest.getAmount()))
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(userToCredit.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {

        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(String.format(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, creditDebitRequest.getAccountNumber()))
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        if (userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()).compareTo(BigDecimal.ZERO) < 0){

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(String.format(AccountUtils.ACCOUNT_INSUFFICIENT_BALANCE_MESSAGE, creditDebitRequest.getAmount()))
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .build())
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
            userRepository.save(userToDebit);

            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEPOSIT")
                    .amount(creditDebitRequest.getAmount())
                    .build();

            transactionService.saveTransaction(transactionRequest);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(userToDebit.getEmail())
                    .subject("WITHDRAW OPERATION IS DONE SUCCESSFULLY")
                    .messageBody("")
                    .messageBody(creditDebitRequest.getAmount() + " of credit is withdrawn from your account! " +
                            "Check out latest deals. \n Your account details: \n" +
                            "Account name: " + userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName() +"\n"
                            + "Account number: " + userToDebit.getAccountNumber())
                    .build();
            emailService.sendEmailAlert(emailDetails);

             return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_WITHDRAW_SUCCESS_CODE)
                    .responseMessage(String.format(AccountUtils.ACCOUNT_WITHDRAW_SUCCESS_MESSAGE, creditDebitRequest.getAmount()))
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest transferRequest) {

        boolean isSourceAccountExist = userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());

        if (!isSourceAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(String.format(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, transferRequest.getSourceAccountNumber()))
                    .accountInfo(null)
                    .build();
        }
        if (!isDestinationAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(String.format(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, transferRequest.getDestinationAccountNumber()))
                    .accountInfo(null)
                    .build();
        }

        User senderUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());
        User recieverUser = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());

        if (senderUser.getAccountBalance().subtract(transferRequest.getAmount()).compareTo(BigDecimal.ZERO) < 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(String.format(AccountUtils.ACCOUNT_INSUFFICIENT_BALANCE_MESSAGE, transferRequest.getAmount()))
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(senderUser.getAccountNumber())
                            .accountBalance(senderUser.getAccountBalance())
                            .accountName(senderUser.getFirstName() + " " + senderUser.getLastName() + " " + senderUser.getOtherName())
                            .build())
                    .build();

        } else {
            senderUser.setAccountBalance(senderUser.getAccountBalance().subtract(transferRequest.getAmount()));
            recieverUser.setAccountBalance(recieverUser.getAccountBalance().add(transferRequest.getAmount()));
            userRepository.save(senderUser);
            userRepository.save(recieverUser);

            TransactionRequest transactionRequest = TransactionRequest.builder()
                    .accountNumber(senderUser.getAccountNumber())
                    .transactionType("TRANSFER")
                    .amount(transferRequest.getAmount())
                    .build();

            transactionService.saveTransaction(transactionRequest);

            TransactionRequest transactionRequest2 = TransactionRequest.builder()
                    .accountNumber(recieverUser.getAccountNumber())
                    .transactionType("TRANSFER")
                    .amount(transferRequest.getAmount())
                    .build();

            transactionService.saveTransaction(transactionRequest2);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(senderUser.getEmail())
                    .subject("TRANSFER IS DONE SUCCESSFULLY")
                    .messageBody("Your " + transferRequest.getAmount() + " amount of transaction is sent to " + recieverUser.getAccountNumber() + " account number successfully!")
                    .build();

            EmailDetails emailDetails2 = EmailDetails.builder()
                    .recipient(recieverUser.getEmail())
                    .subject("TRANSFER ARRIVED SUCCESSFULLY")
                    .messageBody("You have received " + transferRequest.getAmount() + " amount to your deposit from " + senderUser.getAccountNumber() + " account number successfully!" +
                            "\n Use it for good days.")
                    .build();

            emailService.sendEmailAlert(emailDetails);
            emailService.sendEmailAlert(emailDetails2);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_TRANSFER_SUCCESS_CODE)
                    .responseMessage(String.format(AccountUtils.ACCOUNT_TRANSFER_SUCCESS_MESSAGE, transferRequest.getAmount()))
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(senderUser.getAccountNumber())
                            .accountBalance(senderUser.getAccountBalance())
                            .accountName(senderUser.getFirstName() + " " + senderUser.getLastName() + " " + senderUser.getOtherName())
                            .build())
                    .build();
        }
    }
}
