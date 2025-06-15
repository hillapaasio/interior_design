package interiorDesign.logic

import scalafx.scene.paint.Color
import scalafx.scene.shape.{Ellipse, Rectangle, Shape}
import scalafx.scene.paint.Color.Black

//luokka, eikä piirreluokka, jotta voidaan luoda placeholder olio dialogien avuksi
class Draggable(var väri: Color, var a: Int, var b: Int, var c: Int, var d: Int):
  override def toString = "" + väri + " " + a + " " + b + " " + c + " " + d

  //asetetaan placeholder arvot:
  
  var rotation = 0
  
  def drag(x: Double, y: Double): Unit = ()

  val muoto: Shape = Rectangle(0, 0, Black)
  
  //skaalausta varten
  var oikeaLeveys = c
  var oikeaKorkeus = d

  var onTop = false
  var onBottom = false

  var materiaali = "Wood"
  
//luodaan objekti, jotta voidaan käyttää match case rakennetta
object Draggable:
  def unapply(input: Draggable): Option[(Color, Int, Int, Int, Int)] = Some((input.väri, input.a, input.b, input.c, input.d))


class CircFurniture(väri: Color, a: Int, b: Int, c: Int, d: Int) extends Draggable(väri, a, b, c, d):
  override val muoto: Ellipse = new Ellipse:
    centerX = a
    centerY = b
    radiusX = c/2
    radiusY = d/2

  override def drag(x: Double, y: Double) =
    if x >= 0 && y >= 0 then
      muoto.centerX = x
      muoto.centerY = y

class RectObject(väri: Color, a: Int, b: Int, c: Int, d: Int) extends Draggable(väri, a, b, c, d):
  override val muoto: Rectangle = new Rectangle:
    x = a
    y = b
    width = c
    height = d

  override def drag(x: Double, y: Double)  =
    if x >= 0 && y >= 0 then
      val puoletX: Double = (muoto.width.value / 2.0)
      val puoletY: Double = (muoto.height.value / 2.0)
      muoto.x = x - puoletX
      muoto.y = y - puoletY





class Door(väri: Color, a: Int, b: Int, c: Int, d: Int) extends RectObject(väri, a, b, c, d)

class Structure(väri: Color, a: Int, b: Int, c: Int, d: Int) extends RectObject(väri, a, b, c, d)

class RectCarpet(väri: Color, a: Int, b: Int, c: Int, d: Int) extends RectObject(väri, a, b, c, d):
  onBottom = true

class RectLamp(väri: Color, a: Int, b: Int, c: Int, d: Int) extends RectObject(väri, a, b, c, d):
  onTop = true
  
class CircCarpet(väri: Color, a: Int, b: Int, c: Int, d: Int) extends CircFurniture(väri, a, b, c, d):
  onBottom = true

class CircLamp(väri: Color, a: Int, b: Int, c: Int, d: Int) extends CircFurniture(väri, a, b, c, d):
  onTop = true
  

  