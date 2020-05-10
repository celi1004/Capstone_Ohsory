package com.example.ohsoryapp.fragment


import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.ohsoryapp.R
import com.example.ohsoryapp.adapter.AudioFileRecyclerViewAdapter
import com.example.ohsoryapp.data.FileListData
import kotlinx.android.synthetic.main.fragment_filelist.*
import java.io.File
import android.R.attr.path
import java.io.IOException
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class FilelistFragment : Fragment() {

    lateinit var audioFileRecyclerViewAdapter : AudioFileRecyclerViewAdapter

    val dataList: ArrayList<FileListData> by lazy {
        ArrayList<FileListData>()
    }

    var token : String = ""

    var model_name_list = ArrayList<String>()
    var mDirPath = Environment.getExternalStorageDirectory().absolutePath + "/OhSory"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filelist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setSwipeLayout()

        setSpinnser()
    }

    private fun setSwipeLayout(){
        srl_file_list.setOnRefreshListener {
            setSpinnser()
            srl_file_list.isRefreshing = false
        }
    }

    private fun setSpinnser(){
        model_name_list.clear()

        var flag = false

        val directory = File(mDirPath);
        val dir_name_list = directory.listFiles();

        if(dir_name_list==null){
            model_name_list.add("다운받은모델목록이없습니다")
        }else{
            if(dir_name_list.size==0){
                model_name_list.add("다운받은모델목록이없습니다")
            }else{
                flag = true
                for(i in 0 until dir_name_list.size){
                    model_name_list.add(dir_name_list[i].name)
                }
            }
        }

        val arrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, model_name_list)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sp_model_name.adapter = arrayAdapter

        //볼 파일이 있을 때만 클릭리스너 작동
        if(flag){
            sp_model_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    setRecyclerView(model_name_list[position])
                }
            }
        }
    }

    private fun setRecyclerView(folderName : String) {

        dataList.clear()

        val directory = File(mDirPath+'/'+folderName);
        val dir_name_list : Array<File> = directory.listFiles();

        var fpath : String
        var fdate : String = ""
        var ftitle : String

        if(dir_name_list==null){

        }else{
            if(dir_name_list.size==0){

            }else{
                for(i in 0 until dir_name_list.size){
                    //파일들을 하나하나 돌면서 이름, 생성날짜 가져오기
                    fpath = dir_name_list[i].path
                    ftitle = dir_name_list[i].name

                    try{
                        val file = File(fpath)
                        val simpleDataFormat : SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                        fdate = simpleDataFormat.format(file.lastModified())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    dataList.add(FileListData(fpath, fdate, ftitle))
                }
            }
        }

        audioFileRecyclerViewAdapter = AudioFileRecyclerViewAdapter(activity!!, dataList, folderName)

        rv_audio_file.adapter = audioFileRecyclerViewAdapter
        var mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        //최신 파일부터 보여주도록 역순출력
        rv_audio_file.layoutManager = mLayoutManager
    }

}
