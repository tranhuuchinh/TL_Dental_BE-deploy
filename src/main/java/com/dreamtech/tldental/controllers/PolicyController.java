package com.dreamtech.tldental.controllers;

import com.dreamtech.tldental.models.Company;
import com.dreamtech.tldental.models.Policy;
import com.dreamtech.tldental.models.ResponseObject;
import com.dreamtech.tldental.repositories.PolicyRepository;
import com.dreamtech.tldental.services.IStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/api/v1/policy")
public class PolicyController {
    @Autowired
    PolicyRepository repository;
    @Autowired
    private IStorageService storageService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAll() {
        try {
            List<Policy> data = repository.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Query company successfully", data)
            );
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("Failed", exception.getMessage(), "")
            );
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseObject> getDetail(@PathVariable String slug) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Query company successfully", repository.findBySlug(slug))
            );
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("Failed", exception.getMessage(), "")
            );
        }
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createPolicy(@RequestParam ("data") String data,
                                                        @RequestPart("symbol") MultipartFile symbol) {
        try {
            // Convert String to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            Policy policyData = objectMapper.readValue(data, Policy.class);

            // Check existed item
            List<Policy> foundTags = repository.findByName(policyData.getName().trim());
            if (foundTags.size() > 0) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                        new ResponseObject("failed", "Policy's name already taken", "")
                );
            }

            // Check name has "/" or "\"
            if (policyData.getName().contains("/") || policyData.getName().contains("/")) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                        new ResponseObject("failed", "Company's name should not have /", "")
                );
            }

            // Upload image to cloudinary
            String mainImgFileName = storageService.storeFile(symbol);
            policyData.setSymbol(mainImgFileName);
            policyData.setName(policyData.getName().trim());

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Insert company successfully", repository.save(policyData))
            );
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("failed", exception.getMessage(), "")
            );
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deletePolicy(@PathVariable String id) {
        try {
            Optional<Policy> foundpolicy = repository.findById(id);

            if (foundpolicy.isPresent()) {
                storageService.deleteFile(foundpolicy.get().getSymbol());

                repository.deleteById(id);

                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Deleted policy successfully", foundpolicy)
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not find policy with id = " + id, "")
            );
        } catch (Exception exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", exception.getMessage(), "")
            );
        }
    }

    // UPDATE
    @PatchMapping("/{id}")
    ResponseEntity<ResponseObject> updateCompany(@PathVariable String id,
                                                 @RequestPart(value = "symbol", required = false) MultipartFile symbol,
                                                 @RequestParam ("data") String data) throws JsonProcessingException {
        // Convert String to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        Policy policyData = objectMapper.readValue(data, Policy.class);

        Optional<Policy> foundPolicy = repository.findById(id);
        if (foundPolicy.isPresent()) {
            String oldUrlLogo = foundPolicy.get().getSymbol();
            policyData.setCreateAt(foundPolicy.get().getCreateAt());
            policyData.setSymbol(oldUrlLogo);
            // Copy new data
            BeanUtils.copyProperties(policyData, foundPolicy.get());

            // Update img
            if (symbol != null && symbol.getSize() !=0) {
                storageService.deleteFile(oldUrlLogo);
                // Upload image to cloudinary
                String mainImgFileName = storageService.storeFile(symbol);
                foundPolicy.get().setSymbol(mainImgFileName);
            }


            Policy resNews = repository.save(foundPolicy.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Update policy successfully", resNews)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Can not find policy with id = " + policyData.getId(), "")
            );
        }
    }
}
