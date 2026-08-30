package com.example.spendify.domain.model

data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isCustom: Boolean = false,
    val isIncome: Boolean = false,
    val orderIndex: Int = 0
)

object DefaultCategories {
    val expenseCategories = listOf(
        Category(
            id = "cat_food",
            name = "Food & Dining",
            iconName = "restaurant",
            colorHex = "#4edea3",
            orderIndex = 0
        ),
        Category(
            id = "cat_groceries",
            name = "Groceries",
            iconName = "shopping_cart",
            colorHex = "#8083ff",
            orderIndex = 1
        ),
        Category(
            id = "cat_travel",
            name = "Travel",
            iconName = "flight_takeoff",
            colorHex = "#ffb95f",
            orderIndex = 2
        ),
        Category(
            id = "cat_shopping",
            name = "Shopping",
            iconName = "shopping_bag",
            colorHex = "#ffb4ab",
            orderIndex = 3
        ),
        Category(
            id = "cat_bills",
            name = "Bills & Utilities",
            iconName = "receipt_long",
            colorHex = "#c0c1ff",
            orderIndex = 4
        ),
        Category(
            id = "cat_rent",
            name = "Rent & Housing",
            iconName = "home",
            colorHex = "#00a572",
            orderIndex = 5
        ),
        Category(
            id = "cat_emi",
            name = "EMI / Loans",
            iconName = "account_balance",
            colorHex = "#ca8100",
            orderIndex = 6
        ),
        Category(
            id = "cat_subscriptions",
            name = "Subscriptions",
            iconName = "subscriptions",
            colorHex = "#8083ff",
            orderIndex = 7
        ),
        Category(
            id = "cat_education",
            name = "Education",
            iconName = "school",
            colorHex = "#6ffbbe",
            orderIndex = 8
        ),
        Category(
            id = "cat_healthcare",
            name = "Healthcare",
            iconName = "medical_services",
            colorHex = "#ffb4ab",
            orderIndex = 9
        ),
        Category(
            id = "cat_entertainment",
            name = "Entertainment",
            iconName = "movie",
            colorHex = "#c0c1ff",
            orderIndex = 10
        ),
        Category(
            id = "cat_transport",
            name = "Transport",
            iconName = "directions_car",
            colorHex = "#4edea3",
            orderIndex = 11
        ),
        Category(
            id = "cat_gifts",
            name = "Gifts & Donations",
            iconName = "card_giftcard",
            colorHex = "#ffddb8",
            orderIndex = 12
        ),
        Category(
            id = "cat_other_expense",
            name = "Other Expense",
            iconName = "more_horiz",
            colorHex = "#908fa0",
            orderIndex = 13
        )
    )

    val incomeCategories = listOf(
        Category(
            id = "cat_salary",
            name = "Salary",
            iconName = "payments",
            colorHex = "#4edea3",
            isIncome = true,
            orderIndex = 0
        ),
        Category(
            id = "cat_freelance",
            name = "Freelance & Projects",
            iconName = "laptop_mac",
            colorHex = "#c0c1ff",
            isIncome = true,
            orderIndex = 1
        ),
        Category(
            id = "cat_investments",
            name = "Investments & Dividends",
            iconName = "trending_up",
            colorHex = "#ffb95f",
            isIncome = true,
            orderIndex = 2
        ),
        Category(
            id = "cat_business",
            name = "Business Income",
            iconName = "storefront",
            colorHex = "#6ffbbe",
            isIncome = true,
            orderIndex = 3
        ),
        Category(
            id = "cat_other_income",
            name = "Other Income",
            iconName = "account_balance_wallet",
            colorHex = "#ffddb8",
            isIncome = true,
            orderIndex = 4
        )
    )

    val allDefaultCategories = expenseCategories + incomeCategories

    fun getCategoryById(id: String): Category? {
        return allDefaultCategories.find { it.id == id }
    }
}
