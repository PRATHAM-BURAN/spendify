package com.example.spendify.data.repository

import com.example.spendify.data.local.dao.CategoryDao
import com.example.spendify.data.local.entity.CategoryEntity
import com.example.spendify.data.remote.FirestoreService
import com.example.spendify.domain.model.Category
import com.example.spendify.domain.model.DefaultCategories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val firestoreService: FirestoreService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    fun getAllCategoriesFlow(): Flow<List<Category>> {
        return categoryDao.getAllCategoriesFlow().map { entities ->
            if (entities.isEmpty()) {
                DefaultCategories.allDefaultCategories
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    suspend fun getCategoryById(id: String): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
            ?: DefaultCategories.getCategoryById(id)
    }

    suspend fun insertCategory(category: Category, userId: String) {
        categoryDao.insertCategory(CategoryEntity.fromDomain(category))
        scope.launch {
            firestoreService.saveCategory(userId, category)
        }
    }

    suspend fun updateCategory(category: Category, userId: String) {
        categoryDao.updateCategory(CategoryEntity.fromDomain(category))
        scope.launch {
            firestoreService.saveCategory(userId, category)
        }
    }

    suspend fun deleteCategory(category: Category, userId: String) {
        categoryDao.deleteCategoryById(category.id)
        scope.launch {
            firestoreService.deleteCategory(userId, category.id)
        }
    }

    suspend fun ensureDefaultCategoriesSeeded() {
        val existing = categoryDao.getAllCategories()
        if (existing.isEmpty()) {
            val defaults = DefaultCategories.allDefaultCategories.map {
                CategoryEntity.fromDomain(it)
            }
            categoryDao.insertCategories(defaults)
        }
    }
}
