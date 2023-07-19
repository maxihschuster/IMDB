package com.example.imdb5.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {


    val firestoreDB = FirebaseFirestore.getInstance()

    var user = FirebaseAuth.getInstance().currentUser

    val asd : Int = 234
    
/*
    // save address to firebase
    fun saveAddressItem(addressItem: AddressItem): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("users").document(user!!.email.toString())
            .collection("saved_addresses").document(addressItem.addressId)
        return documentReference.set(addressItem)
    }

    // get saved addresses from firebase
    fun getSavedAddress(): CollectionReference {
        var collectionReference = firestoreDB.collection("users/${user!!.email.toString()}/saved_addresses")
        return collectionReference
    }

    fun deleteAddress(addressItem: AddressItem): Task<Void> {
        var documentReference =  firestoreDB.collection("users/${user!!.email.toString()}/saved_addresses")
            .document(addressItem.addressId)
        return documentReference.delete()
    }

 */
}