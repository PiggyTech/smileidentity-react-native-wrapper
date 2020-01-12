//
//  Utilities.swift
//  react-native-smile-identity
//
//  Created by Oyeleke Okiki on 12/31/19.
//

import Foundation

struct Utilities {
    static func saveImageDocumentDirectory(imageName: String, image: UIImage, compressionQuality: CGFloat) -> String{
        let fileManager = FileManager.default
        let path = (NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0] as NSString).appendingPathComponent(imageName)
        let imageData = image.jpegData(compressionQuality: compressionQuality)
        fileManager.createFile(atPath: path as String, contents: imageData, attributes: nil)
        return path
    }
}
