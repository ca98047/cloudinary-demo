package com.musinsa.jinow
// Import the required packages

import com.cloudinary.Cloudinary
import com.cloudinary.Transformation
import com.cloudinary.utils.ObjectUtils
import io.github.cdimascio.dotenv.Dotenv
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL


val dotenv: Dotenv = Dotenv.load()
val cloudinary = Cloudinary(dotenv["CLOUDINARY_URL"])

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Please provide at least one image URL.")
        return
    }

    args.forEach { creativeImage ->

        //원본 다운로드
        downloadInLocal(
            imageUrl = creativeImage,
            localFileName = creativeImage.substringAfterLast("/")
        )

        //upload
        val upload = uploadToCloudinary(creativeImage)

        val publicId = upload["public_id"].toString()
        val format = upload["format"].toString()

        //val crops = listOf("fill", "fill_pad", "auto")
        val crops = listOf("fill", "fill_pad", "auto")
        val gravities = listOf("auto:faces", "auto:object")
        val ratios = listOf("4:3", "5:6")

        val transformList = crops.flatMap { crop ->
            gravities.flatMap { gravity ->
                ratios.map { ratio ->
                    mapOf(
                        //"width" to 650,
                        //"height" to 366,
                        "aspectRatio" to ratio,
                        "gravity" to gravity,
                        "crop" to crop,
                        "background" to if (crop.contains("pad")) "auto" else null
                    )
                }
            }
        }


        transformList.forEach { transformMap ->
            val resourceName = "${
                publicId + "_" + transformMap.filterNot { it.key == "background" }.values
                    .joinToString("_")
            }.$format"

            try {
                //transform
                val transformedImage = transformImage(
                    resourceName = "${publicId}.${upload["format"]}",
                    tranformMap = transformMap
                )

                //download image
                downloadInLocal(
                    imageUrl = transformedImage,
                    localFileName = resourceName
                )
            } catch (e: Exception) {
                e.printStackTrace()
                println("Failed to transform the image.")
            }
        }


        //destroy
        removeCloudinaryImage(publicId)

    }
}

private fun uploadToCloudinary(
    cdnUrl: String,
): MutableMap<Any?, Any?> {
    val params1 = ObjectUtils.asMap(
        "use_filename", true,
        "unique_filename", false,
        "overwrite", true
    )

    //upload
    return cloudinary.uploader().upload(
        cdnUrl,
        params1
    ).also {
        println("Upload successful : $it")
    }
}

private fun transformImage(
    resourceName: String,
    tranformMap: Map<String, Any?>,
): String {

    return cloudinary.url()
        .transformation(
            Transformation<Transformation<*>>()
                .crop(tranformMap["crop"] as String)
                .width(tranformMap["width"] as Int?)
                .height(tranformMap["height"] as Int?)
                .gravity(tranformMap["gravity"] as String?)
                .background(tranformMap["background"] as String?)
                .aspectRatio(tranformMap["aspectRatio"] as String?)
        ).generate(resourceName).also {
            println("Transformed image URL: $it")
        }
}

// Function to download an image from a URL and save it to a file
private fun downloadInLocal(imageUrl: String, localFileName: String) {
    try {
        val url = URL(imageUrl)
        val connection = url.openConnection()
        connection.connect()

        // Open input stream from the connection
        BufferedInputStream(connection.getInputStream()).use { inputStream ->
            // Open file output stream to save the image
            FileOutputStream(File("downloads/$localFileName")).use { outputStream ->
                val dataBuffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                    outputStream.write(dataBuffer, 0, bytesRead)
                }
            }
        }
        println("Image downloaded successfully: $localFileName")
    } catch (e: Exception) {
        e.printStackTrace()
        println("Failed to download the image.")
    }
}

private fun removeCloudinaryImage(publicId: String) {
    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
}
