package com.example.lab08.data

import com.example.lab08.domain.model.Category
import com.example.lab08.domain.model.Product

object MockData {
    val categories = listOf(
        Category(1, "Electrónica", "💻"),
        Category(2, "Ropa", "👕"),
        Category(3, "Hogar", "🏠"),
        Category(4, "Deportes", "⚽")
    )

    val products = listOf(
        Product(1, "Laptop Pro", 1, "Laptop de alto rendimiento con procesador i9 y 32GB RAM", 2999.99, "https://picsum.photos/seed/laptop/400/300"),
        Product(2, "Smartphone X", 1, "Teléfono inteligente con cámara de 108MP y batería de 5000mAh", 1299.99, "https://picsum.photos/seed/phone/400/300"),
        Product(3, "Audífonos BT", 1, "Audífonos inalámbricos con cancelación de ruido activa", 299.99, "https://picsum.photos/seed/headphones/400/300"),
        Product(4, "Camiseta Polo", 2, "Camiseta polo de algodón pima, disponible en varios colores", 49.99, "https://picsum.photos/seed/polo/400/300"),
        Product(5, "Jeans Slim", 2, "Jeans slim fit de mezclilla premium, corte moderno", 89.99, "https://picsum.photos/seed/jeans/400/300"),
        Product(6, "Licuadora Plus", 3, "Licuadora de alta potencia con 10 velocidades y vaso de acero", 149.99, "https://picsum.photos/seed/blender/400/300"),
        Product(7, "Sofá 3 puestos", 3, "Sofá de sala de 3 puestos tapizado en tela premium gris", 899.99, "https://picsum.photos/seed/sofa/400/300"),
        Product(8, "Balón de Fútbol", 4, "Balón oficial FIFA con tecnología de vuelo estabilizado", 69.99, "https://picsum.photos/seed/ball/400/300"),
        Product(9, "Bicicleta MTB", 4, "Bicicleta de montaña con 21 velocidades y frenos de disco", 599.99, "https://picsum.photos/seed/bike/400/300")
    )
}
