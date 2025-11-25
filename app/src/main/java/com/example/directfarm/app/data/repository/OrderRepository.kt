package com.example.directfarm.app.data.repository

import com.example.directfarm.app.data.model.Order
import com.example.directfarm.app.data.model.OrderStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class OrderRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")
    private val productsCollection = firestore.collection("products")

    suspend fun placeOrder(order: Order): Result<String> {
        return try {
            val orderId = ordersCollection.document().id
            val newOrder = order.copy(id = orderId)

            // Create order
            ordersCollection.document(orderId).set(newOrder).await()

            // Update product quantity
            val productDoc = productsCollection.document(order.productId).get().await()
            val currentQuantity = productDoc.getDouble("availableQuantity") ?: 0.0
            val newQuantity = currentQuantity - order.quantity

            productsCollection.document(order.productId)
                .update("availableQuantity", newQuantity)
                .await()

            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update(
                    mapOf(
                        "status" to status.name,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrdersByCustomer(customerId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("customerId", customerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrdersByFarmer(farmerId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("farmerId", farmerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val doc = ordersCollection.document(orderId).get().await()
            val order = doc.toObject(Order::class.java)
                ?: throw Exception("Order not found")
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}