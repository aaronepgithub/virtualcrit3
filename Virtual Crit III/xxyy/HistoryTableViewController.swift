//
//  HistoryTableViewController.swift
//  xxyy
//
//  Created by aaronep on 1/25/18.
//  Copyright © 2018 aaronep. All rights reserved.
//

import UIKit

class HistoryTableViewController: UITableViewController {
    
    var arr = [String]()
    var arrDetail = [String]()
    
    
    @IBOutlet var historyTableView: UITableView!
    
    //    @IBAction func btn_Dismiss(_ sender: UIButton) {
    //        self.dismiss(animated: true, completion: nil)
    //    }
    
    //((rt.int_elapsed_time % 10) == 0 && (rt.int_elapsed_time % 10) > 305)
    
    @objc func updateT() {
        if ((rt.int_elapsed_time % 10) == 0 && rt.int_elapsed_time > (round.secondsPerRound + 5)) {
            self.arr = arrResults
            self.arrDetail = arrResultsDetails
            self.historyTableView.reloadData()
        }
        
        if rt.int_elapsed_time < (round.secondsPerRound + 5) {
         self.arr = ["WAITING...\(round.secondsPerRound - rt.int_elapsed_time)"]
            self.arrDetail = ["..."]
            self.historyTableView.reloadData()
        }
    }
    @objc func updateR() {
        
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        if ((rt.int_elapsed_time % 10) == 0 && rt.int_elapsed_time > 305) {
            self.arr = arrResults
            self.arrDetail = arrResultsDetails
            self.historyTableView.reloadData()
        }
        
        //NotificationCenter.default.addObserver(self, selector: #selector(updateR), name: Notification.Name("newRound"), object: nil)
        
        NotificationCenter.default.addObserver(self, selector: #selector(updateT), name: Notification.Name("update"), object: nil)
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return arr.count
    }
    
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "ID1", for: indexPath)
        
        cell.textLabel?.text = "\(arr[indexPath.row])"
        cell.detailTextLabel?.text = "\(arrDetail[indexPath.row])"
        
        
        return cell
        
        
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "RESULTS"
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.dismiss(animated: true, completion: nil)
        
        tableView.deselectRow(at: indexPath as IndexPath, animated: true)
    }
    /*
     // Override to support conditional editing of the table view.
     override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
     // Return false if you do not want the specified item to be editable.
     return true
     }
     */
    
    /*
     // Override to support editing the table view.
     override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
     if editingStyle == .delete {
     // Delete the row from the data source
     tableView.deleteRows(at: [indexPath], with: .fade)
     } else if editingStyle == .insert {
     // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
     }
     }
     */
    
    /*
     // Override to support rearranging the table view.
     override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {
     
     }
     */
    
    /*
     // Override to support conditional rearranging of the table view.
     override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
     // Return false if you do not want the item to be re-orderable.
     return true
     }
     */
    
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
}
