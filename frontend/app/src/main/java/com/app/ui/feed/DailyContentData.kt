package com.app.ui.feed

object DailyContentData {
    val consejos = listOf(
        "La \"Mise en place\" es sagrada: Antes de encender el fuego, ten todos los ingredientes cortados y medidos; evitarás que algo se queme mientras buscas el bote de sal.",
        "Cuchillos siempre afilados: Un cuchillo desafilado es más peligroso porque requiere más fuerza y es más fácil que resbale.",
        "Seca la carne antes de cocinarla: Si quieres un sellado perfecto y dorado, usa papel de cocina para quitar la humedad de la superficie de la carne antes de que toque la sartén.",
        "No amontones la comida: Si llenas demasiado la sartén, la temperatura bajará y los alimentos se cocerán en su propio vapor en lugar de dorarse.",
        "El poder del ácido: Si a un plato le falta \"algo\" pero ya tiene sal, prueba a añadir unas gotas de limón o vinagre; el ácido realza todos los sabores.",
        "Deja reposar la carne: Al sacar un filete o asado del fuego, espera 5 minutos antes de cortarlo para que los jugos se redistribuyan y no se pierdan en la tabla.",
        "Sal desde la altura: Al salar, hazlo desde unos 20-30 cm de altura para que los granos se distribuyan de forma uniforme por toda la pieza.",
        "Prueba mientras cocinas: Es la única forma de corregir el punto de sal o especias antes de que el plato llegue a la mesa.",
        "Limpia mientras avanzas: Si lavas los utensilios que ya no usas mientras algo se hornea o se guisa, no tendrás una montaña de platos al terminar.",
        "Atesora el agua de la pasta: Antes de escurrirla, guarda una taza del agua de cocción; el almidón que contiene es el secreto para que las salsas se peguen perfectamente a la pasta."
    )

    data class PlatoExotico(
        val nombre: String,
        val ingredientePrincipal: String,
        val pais: String
    )

    val platosExoticos = listOf(
        PlatoExotico("Escamoles", "Larvas de hormiga güijera", "México"),
        PlatoExotico("Fugu", "Pez globo (extremadamente venenoso)", "Japón"),
        PlatoExotico("Hákarl", "Tiburón fermentado", "Islandia"),
        PlatoExotico("Cuy Chactado", "Cobaya (conejillo de indias)", "Perú"),
        PlatoExotico("Balut", "Embrión de pato fertilizado", "Filipinas"),
        PlatoExotico("Casu Marzu", "Queso de oveja con larvas vivas", "Italia (Cerdeña)"),
        PlatoExotico("Sannakji", "Tentáculos de pulpo vivo", "Corea del Sur"),
        PlatoExotico("Witchetty Grub", "Larvas de polilla de madera", "Australia"),
        PlatoExotico("Durian", "Fruta de olor fétido y sabor cremoso", "Sudeste Asiático"),
        PlatoExotico("Surströmming", "Arenque del Báltico fermentado", "Suecia")
    )
}
