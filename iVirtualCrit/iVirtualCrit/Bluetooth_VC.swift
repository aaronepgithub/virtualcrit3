//
//  Bluetooth_VC.swift
//  iVirtualCrit
//
//  Created by aaronep on 1/28/18.
//  Copyright © 2018 aaronep. All rights reserved.
//

import UIKit
import CoreBluetooth


class Bluetooth_VC: UIViewController, CBCentralManagerDelegate, CBPeripheralDelegate, UITableViewDataSource, UITableViewDelegate {
    
    var centralManager: CBCentralManager!
    var arrPeripheral = [CBPeripheral?]()
    var arr_connected_peripherals = [CBPeripheral?]()
    
    let HR_Service = "0x180D"
    let HR_Char =  "0x2A37"
    let CSC_Service = "0x1816"
    let CSC_Char = "0x2A5B"
    
    @IBOutlet weak var BLTE_tableViewOutlet: UITableView!
    
    @IBAction func act_Btn1(_ sender: UIButton) {
        //scan
//        startScanning()
    }
    
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return arrPeripheral.count
    }
    
    
    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        print("Peripheral Connected!!!")
        arr_connected_peripherals.append(peripheral)
        // IMPORTANT: Set the delegate property, otherwise we won't receive the discovery callbacks, like peripheral(_:didDiscoverServices)
        peripheral.delegate = self
        // Now that we've successfully connected to the peripheral, let's discover the services.
        // This time, we will search for the transfer service UUID
        print("Looking for Services for \(String(describing: peripheral.name))...")
        peripheral.discoverServices([CBUUID.init(string: HR_Service), CBUUID.init(string: CSC_Service)])
        
        self.BLTE_tableViewOutlet.reloadData()
        
    }
    
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        print("Discovered Services!!!")
        // Core Bluetooth creates an array of CBService objects —- one for each service that is discovered on the peripheral.
        if let services = peripheral.services {
            for service in services {
                print("Discovered service \(service)")
                // If we found either the transfer service, discover the transfer characteristic
                if (service.uuid == CBUUID(string: HR_Service)) {
                    let transferCharacteristicUUID = CBUUID.init(string: HR_Char)
                    peripheral.discoverCharacteristics([transferCharacteristicUUID], for: service)
                }
                
                if (service.uuid == CBUUID(string: CSC_Service)) {
                    let transferCharacteristicUUID = CBUUID.init(string: CSC_Char)
                    peripheral.discoverCharacteristics([transferCharacteristicUUID], for: service)
                }
            }
        }
    }
    
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        if error != nil {
            print("Error discovering characteristics: \(String(describing: error?.localizedDescription))")
            return
        }
        if let characteristics = service.characteristics {
            for characteristic in characteristics {
                if characteristic.uuid == CBUUID(string: HR_Char) {
                    // subscribe to dynamic changes
                    peripheral.setNotifyValue(true, for: characteristic)
                    print("didDiscoverChar HR for \(peripheral.name!)")
                    //arr_hr_notifying_peripherals.append(peripheral)
                }
                if characteristic.uuid == CBUUID(string: CSC_Char) {
                    // subscribe to dynamic changes
                    peripheral.setNotifyValue(true, for: characteristic)
                    print("didDiscoverChar CSC for \(peripheral.name!)")
                    //arr_CSC_notifying_peripherals.append(peripheral)
                }
            }
        }
    }
    
    
    
    func centralManager(_ central: CBCentralManager, didDiscover peripheral:
        CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
        
        print("didDiscover peripheral \(String(describing: peripheral.name)) at \(RSSI)")
        // check to see if we've already saved a reference to this peripheral
        
        if let firstSuchElement = arrPeripheral.first(where: { $0 == peripheral }) {
            print("\(String(describing: firstSuchElement?.name)) exists")
        } else {
            found_peripheral = peripheral
            arrPeripheral.append(peripheral)
            self.BLTE_tableViewOutlet.reloadData()
        }
    }
    
    func startScanning() {
        print("Started Scanning")
        
        
        if centralManager.isScanning {
            print("Central Manager is already scanning!!")
            return
        } else {
            self.centralManager.scanForPeripherals(withServices: [CBUUID.init(string: CSC_Service), CBUUID.init(string: HR_Service)], options: [CBCentralManagerScanOptionAllowDuplicatesKey:true])
        }
        
        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(3), execute: {
            self.centralManager.stopScan()
            print("Stop Scanning")
            
            
            DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(1), execute: {
                self.BLTE_tableViewOutlet.reloadData()
            })
        })
        
    }
    
    
    
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        if error != nil {
            print("Error updating value for characteristic: \(characteristic) - \(String(describing: error?.localizedDescription))")
            return
        }
        guard characteristic.value != nil else {
            return
        }
        
        func decodeHRValue(withData data: Data) {
//            let count = data.count / MemoryLayout<UInt8>.size
//            var array = [UInt8](repeating: 0, count: count)
//            (data as NSData).getBytes(&array, length:count * MemoryLayout<UInt8>.size)
//            //var bpmValue : Int = 0
//
//
//            if ((array[0] & 0x01) == 0) {
//                bpmValue = Int(array[1])
//
//            } else {
//                //Convert Endianess from Little to Big
//                bpmValue = Int(UInt16(array[2] * 0xFF) + UInt16(array[1]))
//            }
//            rt.rt_hr = Double(bpmValue)
//            rt.rt_score = ((Double(rt.rt_hr) / Double(settings_MAXHR)) * Double(100))
//            out_Top1.setTitle(String(bpmValue), for: .normal)
//            NotificationCenter.default.post(name: Notification.Name("heartrate"), object: nil)
//            out_Btn1.setTitle(String(bpmValue), for: .normal)

        }
        
        
        func decodeCSC(withData data : Data) {
//            let WHEEL_REVOLUTION_FLAG               : UInt8 = 0x01
//            let CRANK_REVOLUTION_FLAG               : UInt8 = 0x02
//            let value = UnsafeMutablePointer<UInt8>(mutating: (data as NSData).bytes.bindMemory(to: UInt8.self, capacity: data.count))
//            //dump(value)
//            let flag = value[0]
//
//            if flag & WHEEL_REVOLUTION_FLAG == 1 {
//                //print("SPD value[1]");print(value[1])
//                //out_Top2.setTitle(String(value[1]), for: .normal)
//                //processWheelData(withData: data)
//                if flag & CRANK_REVOLUTION_FLAG == 2 {
//                    out_Top3.setTitle(String(value[7]), for: .normal)
//                    //print("CAD value[7]");print(value[7])
//                    processCrankData(withData: data, andCrankRevolutionIndex: 7)
//                }
//            } else {
//                if flag & CRANK_REVOLUTION_FLAG == 2 {
//                    out_Top3.setTitle(String(value[1]), for: .normal)
//                    //print("CAD value[1]");print(value[1])
//                    processCrankData(withData: data, andCrankRevolutionIndex: 1)
//                }
//            }
        }
        
        
        
        if characteristic.uuid == CBUUID(string: HR_Char) {
            guard characteristic.value != nil else {
                print("Characteristic Value is nil on this go-round")
                return
            }
            
            if error != nil {
                print("Error updating value for characteristic: \(characteristic) - \(String(describing: error?.localizedDescription))")
                return
            }
            //decodeHRValue(withData: characteristic.value!)
        }
        
        if characteristic.uuid == CBUUID(string: CSC_Char) {
            guard characteristic.value != nil else {
                print("Characteristic Value is nil on this go-round")
                return
            }
            
            if error != nil {
                print("Error updating value for characteristic: \(characteristic) - \(String(describing: error?.localizedDescription))")
                return
            }
            
            decodeCSC(withData: characteristic.value!)
            //print("didUpdateValue for:  \(peripheral.name!).\nService is:  \(characteristic.service.uuid).\nCharacteristic is:  \(characteristic.uuid).")
            
        }
        
        
        
        
        
    }
    
    func disconnectAllPeripherals() {
        dump(arrPeripheral)
        for p in arrPeripheral {
            // verify we have a peripheral
            
            // check to see if the peripheral is connected
            if p?.state != .connected {
                print("Peripheral exists but is not connected.")
                return
            }
            guard let services = p?.services else {
                print("Cancel Peripheral Connection")
                centralManager.cancelPeripheralConnection(p!)  //no services
                return
            }
            for service in services {
                // iterate through characteristics
                if let characteristics = service.characteristics {
                    for characteristic in characteristics {
                        // find the Transfer Characteristic we defined in our Device struct
                        if characteristic.uuid == CBUUID.init(string: CSC_Char) {
                            p?.setNotifyValue(false, for: characteristic)
                            print("set Notify Value to False for:  \(String(describing: p?.name))")
                            return
                        }
                        if characteristic.uuid == CBUUID.init(string: HR_Char) {
                            p?.setNotifyValue(false, for: characteristic)
                            print("set Notify Value to False for:  \(String(describing: p?.name))")
                            return
                        }
                    }
                }
            }
            // disconnect from the peripheral
            centralManager.cancelPeripheralConnection(p!)
        }
        arr_connected_peripherals = []
    }
    
    func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?){
        print("Did Disconnect Peripheral")
        
        // check to see if the peripheral is connected
        print("did disconnect peripheral:  \(String(describing: peripheral.name))")
        if peripheral.state != .connected {
            print("Peripheral exists but is not connected.")
            //put rescan code here
            centralManager.connect(peripheral, options: nil)
            self.BLTE_tableViewOutlet.reloadData()
            return
        }
        
        guard let services = peripheral.services else {
            // disconnect directly
            centralManager.cancelPeripheralConnection(peripheral)
            print("Cancel Peripheral Connection")
            self.BLTE_tableViewOutlet.reloadData()
            return
        }
        
        for service in services {
            // iterate through characteristics
            if let characteristics = service.characteristics {
                for characteristic in characteristics {
                    
                    if characteristic.uuid == CBUUID.init(string: HR_Char) {
                        peripheral.setNotifyValue(false, for: characteristic)
                        print("set Notify Value to False")
                        return
                    }
                    if characteristic.uuid == CBUUID.init(string: CSC_Char) {
                        peripheral.setNotifyValue(false, for: characteristic)
                        print("set Notify Value to False")
                        return
                    }
                    
                }
            }
        }
        centralManager.cancelPeripheralConnection(peripheral)
        print("Cancel Connection")
        self.BLTE_tableViewOutlet.reloadData()
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "R1") as! BLTE_TableViewCell
        
        let str = arrPeripheral[indexPath.row]?.name
        var strr: String = String(describing: arrPeripheral[indexPath.row]!.identifier)
        
        switch(arrPeripheral[indexPath.row]!.state) {
        case .connected:
            strr = "Connected"
        case .disconnected:
            strr = "Disconnected"
        case .connecting:
            strr = "Connecting"
        case .disconnecting:
            strr = "Disconnecting"
        }
        
        cell.BLTE_CellTitle.text = str
        cell.BLTE_CellSubTitle.text = String(describing: strr)
        
        return cell
    }
    
    
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        centralManager?.connect(arrPeripheral[indexPath.row]!, options: nil)
        tableView.deselectRow(at: indexPath, animated: true)
        self.BLTE_tableViewOutlet.reloadData()
    }
    
    var found_peripheral: CBPeripheral?
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        print("Central Manager State Updated: \(central.state)")
        // if Bluetooth is on, proceed...
        if central.state != .poweredOn {
            found_peripheral = nil
            return
        }
        self.BLTE_tableViewOutlet.reloadData()
    }
    
    
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        centralManager = CBCentralManager(delegate: self, queue: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
