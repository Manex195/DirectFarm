package com.example.directfarm.app.ui.farmer

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.directfarm.app.data.model.Order
import com.example.directfarm.app.data.model.OrderStatus
import com.example.directfarm.app.data.model.Product
import com.example.directfarm.app.data.repository.OrderRepository
import com.example.directfarm.app.data.repository.ProductRepository
import kotlinx.coroutines.launch

class FarmerViewModel : ViewModel() {

    private val productRepository = ProductRepository()
    private val orderRepository = OrderRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    private val _operationResult = MutableLiveData<Result<String>>()
    val operationResult: LiveData<Result<String>> = _operationResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadProducts(farmerId: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = productRepository.getProductsByFarmer(farmerId)
            result.onSuccess { _products.value = it }
            _loading.value = false
        }
    }

    fun loadOrders(farmerId: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = orderRepository.getOrdersByFarmer(farmerId)
            result.onSuccess { _orders.value = it }
            _loading.value = false
        }
    }

    fun addProduct(product: Product, imageUri: Uri?) {
        _loading.value = true
        viewModelScope.launch {
            val result = productRepository.addProduct(product, imageUri)
            _operationResult.value = result.map { "Product added successfully" }
            _loading.value = false
        }
    }

    fun updateProduct(product: Product, imageUri: Uri?) {
        _loading.value = true
        viewModelScope.launch {
            val result = productRepository.updateProduct(product, imageUri)
            _operationResult.value = result.map { "Product updated successfully" }
            _loading.value = false
        }
    }

    fun deleteProduct(productId: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = productRepository.deleteProduct(productId)
            _operationResult.value = result.map { "Product deleted successfully" }
            _loading.value = false
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            val result = orderRepository.updateOrderStatus(orderId, status)
            result.onSuccess {
                _operationResult.value = Result.success("Order status updated")
            }
        }
    }
}