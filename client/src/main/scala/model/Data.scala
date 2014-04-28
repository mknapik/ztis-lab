package model

case class Data(id: Int, `type`: String, data: String)

case class DataWrapper(data: Data)
