package com.example.directfarm.app.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.directfarm.app.data.model.Order
import com.example.directfarm.app.data.model.Product
import com.example.directfarm.app.data.repository.OrderRepository
import com.example.directfarm.app.data.repository.ProductRepository
import kotlinx.coroutines.launch

class CustomerViewModel : ViewModel() {

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

    fun loadAllProducts() {
        _loading.value = true
        viewModelScope.launch {
            val result = productRepository.getAllProducts()
            result.onSuccess { _products.value = it }
            _loading.value = false
        }
    }

    fun searchProducts(query: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = productRepository.searchProducts(query)
            result.onSuccess { _products.value = it }
            _loading.value = false
        }
    }

    fun loadOrders(customerId: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = orderRepository.getOrdersByCustomer(customerId)
            result.onSuccess { _orders.value = it }
            _loading.value = false
        }
    }

    fun placeOrder(order: Order) {
        _loading.value = true
        viewModelScope.launch {
            val result = orderRepository.placeOrder(order)
            _operationResult.value = result.map { "Order placed successfully!" }
            _loading.value = false
        }
    }
}