package com.example.fileservice.service;

import com.example.fileservice.entity.FileUser;
import com.example.fileservice.entity.UserPubKey;
import com.example.fileservice.repository.FileUserRepository;
import com.example.fileservice.repository.UserPubKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionService {
    UserPubKeyRepository userPubKeyRepository;
    FileUserRepository fileUserRepository;
    public void registerUserPubKey(String username, String pubKey){


        try {
            PublicKey key = getKey(pubKey);
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException e){
            throw new IllegalArgumentException("");
        }

        FileUser user = fileUserRepository
                .getFileUserByUsername(username)
                .orElseThrow(IllegalAccessError::new);

        UserPubKey userPubKey = UserPubKey
                .builder()
                .user(user)
                .pubKey(pubKey)
                .build();

        userPubKeyRepository.save(userPubKey);
    }

    public PublicKey getKey(String key) throws InvalidKeySpecException,NoSuchAlgorithmException  {
                //if base64 is invalid, you will see an error here
                byte[] byteKey = Base64.getDecoder().decode(key);

                //if it is not in RSA public key format, you will see error here as java.security.spec.InvalidKeySpecException
                X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
                KeyFactory kf = KeyFactory.getInstance("RSA");
               return kf.generatePublic(X509publicKey);
    }
}
