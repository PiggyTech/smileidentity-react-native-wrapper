//
//  SmileIdentity.swift
//  SmileIdentity
//
//  Created by Oyeleke Okiki on 12/26/19.
//

import Smile_Identity_SDK

@objc(SmileIdentity)
class SmileIdentity: RCTEventEmitter {

    private var captureSelfie: CaptureSelfie?
    private var previewBackgroundView: UIView!
    private var cancelButton: UIButton!
    private var previewView: VideoPreviewView!
    private var userTag: String!
    private var compressionQuality: CGFloat!
    private weak var controllerReference: UIViewController!
    private var onCompleteCallback: RCTResponseSenderBlock?

    override init() {
        super.init()
        EventEmitter.sharedInstance.registerEventEmitter(eventEmitter: self)
    }

    @objc open override func supportedEvents() -> [String] {
        return EventEmitter.sharedInstance.allEvents
    }

    @objc(captureSelfie:onCompleteCallback:)
    public func captureSelfie(options: NSDictionary = [:],
                              _ onCompleteCallback: @escaping RCTResponseSenderBlock){

        let cameraFacingFront = options["cameraFacingFront"] as? Bool ?? true
        let captureSmileManually = options["captureSmileManually"] as? Bool ?? true

        self.userTag = options["fileName"] as? String ?? SmileIDSingleton.DEFAULT_USER_TAG
        self.compressionQuality = options["photoQuality"] as? CGFloat ?? 1.0
        self.onCompleteCallback = onCompleteCallback

        DispatchQueue.main.async { [unowned self] in
            guard let rootViewController = UIApplication.shared.delegate?.window??.rootViewController else {
                return
            }
            self.controllerReference = rootViewController;
            let controllerViewBounds = self.controllerReference.view.bounds
            let captureRect = CGRect(x: 0, y: (controllerViewBounds.maxY - controllerViewBounds.width)/2, width: controllerViewBounds.width, height: controllerViewBounds.width) //Using a square so it makes sense

            self.setupCameraViewsWithRects(captureRect, controllerViewBounds: controllerViewBounds)
            self.addCancelButton()

            self.captureSelfie = CaptureSelfie()
            self.captureSelfie?.setup(
                captureSelfieDelegate: self,
                previewView: self.previewView,
                useFrontCamera: cameraFacingFront,
                doFlashScreenOnShutter: false)
            self.captureSelfie?.manualCapture( isManualCapture: captureSmileManually )

            self.captureSelfie?.start( screenRect: captureRect, userTag: self.userTag )
        }
    }

    private func setupCameraViewsWithRects(_ captureRect: CGRect, controllerViewBounds: CGRect) {
        self.previewView = VideoPreviewView()
        self.previewBackgroundView = UIView()

        self.previewBackgroundView.frame = controllerViewBounds
        self.previewBackgroundView.backgroundColor = .black
        self.previewView.backgroundColor = .black

        self.previewView.frame = captureRect; //Adjustable

        self.previewBackgroundView.addSubview(self.previewView)
        self.controllerReference.view.addSubview(self.previewBackgroundView)
    }

    private func addCancelButton() {
        cancelButton = UIButton(type: UIButton.ButtonType.system) as UIButton
        let buttonWidth: CGFloat = 75;
        cancelButton.frame = CGRect(x: self.controllerReference.view.frame.width - 42 - buttonWidth/2, y: 24, width: buttonWidth, height: 105)
        cancelButton.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        cancelButton.setTitle("Cancel", for: .normal)
        cancelButton.setTitleColor(.white, for: .normal)
        cancelButton.addTarget(self, action: #selector(stopCapturing), for: .touchUpInside)

        self.controllerReference.view.addSubview(cancelButton)
    }

    @objc(pauseCapturing)
    public func pauseCapturing() {
        DispatchQueue.main.async { [unowned self] in
            guard self.captureSelfie != nil else {
                return; }
            self.captureSelfie!.stop();
        }

    }
    
    @objc(stopCapturing)
    public func stopCapturing() {
        DispatchQueue.main.async { [unowned self] in
            guard self.captureSelfie != nil else {
                return; }
            self.captureSelfie!.stop()
            self.cancelButton.removeFromSuperview()
            self.previewView.removeFromSuperview()
            self.previewBackgroundView.removeFromSuperview();
        }

    }
}

extension SmileIdentity: CaptureSelfieDelegate {
    func onComplete(previewUIImage: UIImage?) {
        guard let image = previewUIImage else {
            //TODO: Fatal error?
            EventEmitter.sharedInstance.dispatch(name: "Error", body: "onComplete with missing image")
            return
        }
        let imagePath = Utilities.saveImageDocumentDirectory(imageName: self.userTag, image: image, compressionQuality: self.compressionQuality)

       let fileManager = FileManager.default
        if fileManager.fileExists(atPath: imagePath) {
            print("FILE AVAILABLE")
        } else {
            print("FILE NOT AVAILABLE")
        }
        onCompleteCallback?([imagePath])
    }

    func onError(sidError: SIDError) {
        EventEmitter.sharedInstance.dispatch(name: "Error", body: sidError.localizedDescription)
    }

    func onFaceStateChanged(faceState: Int) {
        EventEmitter.sharedInstance.dispatch(name: "FaceChanged", body: faceState)
    }
}
