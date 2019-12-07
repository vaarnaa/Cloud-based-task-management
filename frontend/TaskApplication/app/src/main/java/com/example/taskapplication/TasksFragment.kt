package com.example.taskapplication

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_project.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent





// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TasksFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TasksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TasksFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var taskAdapter: TasksCustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = activity as Context
        val taskEntries = arrayListOf<Map<String, String>>()
        taskAdapter = TasksCustomAdapter(activity, taskEntries)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*val fab = getView()?.findViewById(R.id.fab_project) as FloatingActionButton
        fab.setOnClickListener(this)
        val listView = projectTasksView
        listView.adapter = taskAdapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                // Update the status of a clicked task using our API.
                Log.d(TAG, "position $position")
                Log.d(TAG, "getItemAtPosition ${parent.getItemAtPosition(position)}")
                val id = taskAdapter.getItem(position)!!.getValue("tid")
                val st = taskAdapter.getItem(position)!!.getValue("status")
                (activity as ProjectActivity).updateTask(id, st)
            }

         */
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_project -> createTask()
        }
    }

    private fun createTask() {
        val activity = activity as ProjectActivity
        val intent = Intent(activity, CreateTaskActivity::class.java)
        intent.putExtra("pid", activity.projectId)
        (activity).startActivity(intent)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(): TasksFragment = TasksFragment()
        const val TAG = "TasksFragment"
    }
}
