class EventEmitter {
    /// Shared Instance.
    public static var sharedInstance = EventEmitter()

    // SmileIdentity is instantiated by React Native with the bridge.
    private static var eventEmitter: SmileIdentity!

    private init() {}

    // When React Native instantiates the emitter it is registered here.
    func registerEventEmitter(eventEmitter: SmileIdentity) {
        EventEmitter.eventEmitter = eventEmitter
    }

    func dispatch(name: String, body: Any?) {
      EventEmitter.eventEmitter.sendEvent(withName: name, body: body)
    }

    lazy var allEvents: [String] = {
        return ["FaceChanged", "Error"]
    }()
    
}
