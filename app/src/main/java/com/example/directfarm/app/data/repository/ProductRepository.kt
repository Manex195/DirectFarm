package com.example.directfarm.app.data.repository

import android.net.Uri
import com.example.directfarm.app.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val productsCollection = firestore.collection("products")

    suspend fun addProduct(product: Product, imageUri: Uri?): Result<String> {
        return try {
            val productId = productsCollection.document().id
            var imageUrl = ""

            if (imageUri != null) {
                imageUrl = uploadImage(imageUri)
            }

            val newProduct = product.copy(
                id = productId,
                imageUrl = imageUrl,
                updatedAt = System.currentTimeMillis()
            )

            productsCollection.document(productId).set(newProduct).await()
            Result.success(productId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product, imageUri: Uri?): Result<Unit> {
        return try {
            var imageUrl = product.imageUrl

            if (imageUri != null) {
                imageUrl = uploadImage(imageUri)
            }

            val updatedProduct = product.copy(
                imageUrl = imageUrl,
                updatedAt = System.currentTimeMillis()
            )

            productsCollection.document(product.id).set(updatedProduct).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByFarmer(farmerId: String): Result<List<Product>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("farmerId", farmerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val products = snapshot.toObjects(Product::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val snapshot = productsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val products = snapshot.toObjects(Product::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val doc = productsCollection.document(productId).get().await()
            val product = doc.toObject(Product::class.java)
                ?: throw Exception("Product not found")
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            val snapshot = productsCollection.get().await()
            val products = snapshot.toObjects(Product::class.java)
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.category.contains(query, ignoreCase = true) ||
                            it.farmerLocation.contains(query, ignoreCase = true)
                }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadImage(uri: Uri): String {
        val fileName = "products/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}