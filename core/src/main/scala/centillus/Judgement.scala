package centillus

sealed trait Judgement
case object Perfect extends Judgement
case object Great extends Judgement
case object Good extends Judgement
case object Bad extends Judgement
case object Poor extends Judgement
case object Miss extends Judgement
case object Space extends Judgement
