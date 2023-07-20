package net.bandithemepark.bandicore.server.animatronics

import com.google.gson.JsonObject
import org.joml.Matrix4f
import java.util.UUID

class AnimatronicNodePose(nodeJson: JsonObject) {

    val uuid: UUID
    val matrix: Matrix4f

    init {
        uuid = UUID.fromString(nodeJson.get("uuid").asString)
        matrix = Matrix4f()

        val matrixJson = nodeJson.get("matrix").asJsonArray
        matrix.m00(matrixJson[0].asFloat)
        matrix.m01(matrixJson[1].asFloat)
        matrix.m02(matrixJson[2].asFloat)
        matrix.m03(matrixJson[3].asFloat)
        matrix.m10(matrixJson[4].asFloat)
        matrix.m11(matrixJson[5].asFloat)
        matrix.m12(matrixJson[6].asFloat)
        matrix.m13(matrixJson[7].asFloat)
        matrix.m20(matrixJson[8].asFloat)
        matrix.m21(matrixJson[9].asFloat)
        matrix.m22(matrixJson[10].asFloat)
        matrix.m23(matrixJson[11].asFloat)
        matrix.m30(matrixJson[12].asFloat)
        matrix.m31(matrixJson[13].asFloat)
        matrix.m32(matrixJson[14].asFloat)
        matrix.m33(matrixJson[15].asFloat)
    }
}