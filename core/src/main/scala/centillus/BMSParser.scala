package centillus

import scala.util.parsing.combinator.RegexParsers

class BMSParser extends RegexParsers {
  def player = "PLAYER" ~ """[1-4]""".r ^^ {
    case "PLAYER" ~ number => Player(number.toInt)
  }
  def genre = "GENRE" ~ """(.*)""".r ^^ {
    case "GENRE" ~ name => Genre(name)
  }
  def title = "TITLE" ~ """(.*)""".r ^^ {
    case "TITLE" ~ name => Title(name)
  }
  def artist = "ARTIST" ~ """(.*)""".r ^^ {
    case "ARTIST" ~ name => Artist(name)
  }
  def bpm = "BPM" ~ """\d{3}""".r ^^ {
    case "BPM" ~ tempo => BPM(tempo.toDouble)
  }
  def playlevel = "PLAYLEVEL" ~ """\d{1,}""".r ^^ {
    case "PLAYLEVEL" ~ level => Playlevel(level.toInt)
  }
  def rank = "RANK" ~ """[0-3]""".r ^^ {
    case "RANK" ~ number => Rank(number.toInt)
  }
  def total = "TOTAL" ~ """\d{1,}""".r ^^ {
    case "TOTAL" ~ amount => Total(amount.toInt)
  }
  def stagefile = "STAGEFILE" ~ """(.*)""".r ^^ {
    case "STAGEFILE" ~ filename => Stagefile(filename)
  }
  def wav = "WAV" ~ """[0-9A-Z]{2}""".r ~ """(.*)""".r ^^ {
    case "WAV" ~ number ~ filename => WAV(number, filename)
  }
  def bmp = "BMP" ~ """[0-9A-Z]{2}""".r ~ """(.*)""".r ^^ {
    case "BMP" ~ number ~ filename => BMP(number, filename)
  }
  def header: Parser[BMSObject] = "#" ~ ( player
                                        | genre
                                        | title
                                        | artist
                                        | bpm
                                        | playlevel
                                        | rank
                                        | total
                                        | stagefile
                                        | wav
                                        | bmp
                                        ) ^^ {
    case "#" ~ info => info
  }

  def barLit = """[0-9]{3}""".r
  def chanLit = """[0-9]{2}""".r
  def decLit = """([0-9]+\.[0-9]+)""".r
  def objsLit = """([0-9A-Z]{2})+""".r
  def data: Parser[Data] = "#" ~ barLit ~ chanLit ~ ":" ~ (objsLit | decLit) ^^ {
    case "#" ~ bar ~ chan ~ ":" ~ objs => {
      Data(bar.toInt, chan.toInt, objs)
    }
  }

  def comment: Parser[BMSNil] = "*" ~ raw".*".r ^^ { case _ => BMSNil() }

  def expr: Parser[List[BMSObject]] = (header | data | comment).*

  def parse(input: String): Either[String, List[BMSObject]] =
    parseAll(expr, input) match {
      case Success(data, next) => {
        println(data)
        Right(data)
      }
      case NoSuccess(err, next) =>
        Left(s"$err on line ${next.pos.line} on column ${next.pos.column}")
    }
}
