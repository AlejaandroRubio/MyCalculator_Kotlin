package uneat.aler.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var textViewResultado: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewResultado = findViewById(R.id.textViewResultado)

    }

    fun calcular(view: View) {

        var boton = view as Button
        var textoBoton = boton.text.toString()

        var concatenar = textViewResultado?.text.toString() + textoBoton
        var mostrar = ceroIzq(concatenar)

        if(textoBoton == "Del"){

            if (textViewResultado?.text.toString().length > 1) {
                textViewResultado?.text = textViewResultado?.text.toString().substring(0, textViewResultado?.text.toString().length - 1)
            } else {
                textViewResultado?.text = "0"
            }
        } else if (textoBoton == "=") {
            var resultado = 0.0

            try {

                var e = 7
                resultado = eval(textViewResultado?.text.toString())

                if (resultado.toString().length > e) {
                    textViewResultado?.text = resultado.toString().substring(0, e)
                    var DRespuesta= "60sp"
                } else {
                    textViewResultado?.text = resultado.toString()
                    var DRespuesta= "80sp"

                }

            } catch (e: Exception) {
                textViewResultado?.text = "Error"
            }

        } else if (textoBoton == "C") {
            textViewResultado?.text = "0"
        } else {
            textViewResultado?.text = mostrar
        }

    }

    fun ceroIzq(str: String): String {
        var i = 0
        while (i < str.length && str[i] == '0') i++
        val sb = StringBuilder(str)
        sb.replace(0, i, "")
        return sb.toString()
    }

    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() {
                ch = if (++pos < str.length) str[pos].toInt() else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.toInt())) x += parseTerm() // addition
                    else if (eat('-'.toInt())) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.toInt())) x *= parseFactor() // multiplication
                    else if (eat('/'.toInt())) x /= parseFactor() // division
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.toInt())) return parseFactor() // unary plus
                if (eat('-'.toInt())) return -parseFactor() // unary minus
                var x: Double
                val startPos = pos
                if (eat('('.toInt())) { // parentheses
                    x = parseExpression()
                    eat(')'.toInt())
                } else if (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) { // numbers
                    while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) nextChar()
                   // x = str.substring(startPos, pos).toDouble()
                    x = if (str.substring(startPos, pos).contains(".")) str.substring(startPos, pos).toDouble() else str.substring(startPos, pos).toInt().toDouble()
                } else if (ch >= 'a'.toInt() && ch <= 'z'.toInt()) { // functions
                    while (ch >= 'a'.toInt() && ch <= 'z'.toInt()) nextChar()
                    val func = str.substring(startPos, pos)
                    x = parseFactor()
                    x = if (func == "sqrt") Math.sqrt(x) else if (func == "sin") Math.sin(
                        Math.toRadians(x)
                    )else if (func == "cos") Math.cos(Math.toRadians(x))
                    else if (func == "tan") Math.tan(Math.toRadians(x))
                    else if (func =="log") Math.log10(x)
                    else if(func =="ln") Math.log(x)



                    else throw RuntimeException("Unknown function: $func")
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                if (eat('^'.toInt())) x = Math.pow(x, parseFactor()) // exponentiation
                if (eat('!'.toInt())) x = factorial(x.toInt()).toDouble()
                return x
            }

            fun factorial(x: Int): Int {
                return if (x==0){
                    1
                }else{
                    x *  factorial(x-1)
                }
            }


        }.parse()
    }
}