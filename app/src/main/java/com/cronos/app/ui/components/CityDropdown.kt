package com.cronos.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val RUSSIAN_CITIES = listOf(
    "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань",
    "Нижний Новгород", "Челябинск", "Самара", "Омск", "Ростов-на-Дону",
    "Уфа", "Красноярск", "Воронеж", "Пермь", "Волгоград",
    "Краснодар", "Саратов", "Тюмень", "Тольятти", "Ижевск",
    "Барнаул", "Ульяновск", "Иркутск", "Хабаровск", "Ярославль",
    "Владивосток", "Махачкала", "Томск", "Оренбург", "Кемерово",
    "Новокузнецк", "Рязань", "Астрахань", "Набережные Челны", "Пенза",
    "Липецк", "Тула", "Киров", "Чебоксары", "Калининград",
    "Брянск", "Курск", "Иваново", "Магнитогорск", "Тверь",
    "Ставрополь", "Белгород", "Сочи", "Нижний Тагил", "Архангельск",
    "Владимир", "Симферополь", "Севастополь", "Сургут", "Мурманск",
    "Улан-Удэ", "Чита", "Смоленск", "Орёл", "Волжский",
    "Череповец", "Вологда", "Саранск", "Якутск", "Тамбов",
    "Грозный", "Стерлитамак", "Кострома", "Нижневартовск", "Новороссийск",
    "Йошкар-Ола", "Мытищи", "Химки", "Балашиха", "Подольск",
    "Люберцы", "Королёв", "Нальчик", "Благовещенск", "Сыктывкар",
    "Петрозаводск", "Псков", "Великий Новгород", "Калуга", "Курган",
    "Орск", "Ангарск", "Нижнекамск", "Старый Оскол", "Энгельс",
    "Рыбинск", "Балаково", "Прокопьевск", "Армавир", "Южно-Сахалинск",
    "Комсомольск-на-Амуре", "Петропавловск-Камчатский", "Абакан", "Элиста", "Майкоп",
    "Горно-Алтайск", "Кызыл", "Магас", "Нарьян-Мар", "Салехард",
    "Ханты-Мансийск", "Анадырь", "Биробиджан"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val filtered = remember(value) {
        if (value.isBlank()) RUSSIAN_CITIES
        else RUSSIAN_CITIES.filter { it.contains(value, ignoreCase = true) }
    }

    ExposedDropdownMenuBox(
        expanded = expanded && filtered.isNotEmpty(),
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            label = { Text("Город") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded && filtered.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 250.dp)
        ) {
            filtered.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city) },
                    onClick = {
                        onValueChange(city)
                        expanded = false
                    }
                )
            }
        }
    }
}
