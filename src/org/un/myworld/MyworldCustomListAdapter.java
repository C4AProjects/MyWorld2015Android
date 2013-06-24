package org.un.myworld;

import java.util.List;

import org.un.imports.ModelListItem;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MyworldCustomListAdapter extends ArrayAdapter<ModelListItem>{

	private final List<ModelListItem> list;
	private final Activity context;
	
	//boolean checkAll_flag = false;
	//boolean checkItem_flag = false;
	
	//Constructor
	public MyworldCustomListAdapter(Activity context, List<ModelListItem> list) {
		super(context, R.layout.option_row, list);
		this.context = context;
		this.list = list;
	}
	
	static class ViewHolder {
		protected TextView text_code,text_title,text_desc;
		protected ImageView img_mdgColor;
		protected CheckBox checkbox;
	}
	
	 @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater inflator = context.getLayoutInflater();
				convertView = inflator.inflate(R.layout.option_row, null);
				viewHolder = new ViewHolder();
				viewHolder.text_code = (TextView) convertView.findViewById(R.id.option_code);
				viewHolder.text_title = (TextView) convertView.findViewById(R.id.mdg_title);
				viewHolder.text_desc = (TextView) convertView.findViewById(R.id.mdg_description);
				viewHolder.img_mdgColor = (ImageView) convertView.findViewById(R.id.option_color);
				viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.mdg_check);
				final CheckBox cbo=(CheckBox) convertView.findViewById(R.id.mdg_check);
				
				//on checkbox state change listener
				
				viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
					int getPosition = (Integer) checkBox.getTag();  // Here we get the position that we have set for the checkbox using setTag.
					list.get(getPosition).setSelected(checkBox.isChecked());
					Log.i("Code :",""+list.get(getPosition).getOptionCode());
					
					if(isChecked){
			                if (cbo.isClickable()) {
			                	VotingActivity.countVotes = VotingActivity.countVotes + 1;
			                	Log.i("Count: ",""+VotingActivity.countVotes);
			                }
			            } else if (!cbo.isChecked()) {
			                if (cbo.isClickable()) {
			                    if (VotingActivity.countVotes > 6)
			                    	VotingActivity.countVotes--;
			                }
			            }
					
					//list.get(getPosition).isSelected();
	               }
				
			});
				
				convertView.setTag(viewHolder);
				convertView.setTag(R.id.mdg_title, viewHolder.text_title);
				convertView.setTag(R.id.mdg_description, viewHolder.text_desc);
				convertView.setTag(R.id.option_code, viewHolder.text_code);
				convertView.setTag(R.id.option_color, viewHolder.img_mdgColor);
				convertView.setTag(R.id.mdg_check, viewHolder.checkbox);
				
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			//temporarily remove state change listener
			viewHolder.checkbox.setOnCheckedChangeListener(null);
			viewHolder.checkbox.setTag(position); // retain state of the checkbox
			
			viewHolder.text_code.setText(list.get(position).getOptionCode());
			viewHolder.text_title.setText(list.get(position).getTitle());
			viewHolder.text_desc.setText(list.get(position).getDescription());
			viewHolder.img_mdgColor.setImageResource(context.getResources().getIdentifier(list.get(position).getOptionColor() , "drawable", context.getPackageName()));
			viewHolder.checkbox.setChecked(list.get(position).isSelected());

			//refire the state change listener
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
					int getPosition = (Integer) checkBox.getTag();  // Here we get the position that we have set for the checkbox using setTag.
					list.get(getPosition).setSelected(checkBox.isChecked()); //set the state of the change listener
					//list.get(getPosition).isSelected();
					
	                 }
			});
			
			//notifyDataSetChanged();
			
			for(int i=0; i<list.size();i++){
            	if(list.get(i).isSelected()==true){
            		//i++;
            		VotingActivity.countVotes=(i+1);
            		//Log.i("Picked Priority: "+VotingActivity.countVotes,"Code"+list.get(i).getOptionCode());
            		 Log.i("Vote count at: "," "+VotingActivity.countVotes);
            	}else{
            		VotingActivity.countVotes=(i-1);
            	}
            }
			
			return convertView;
		}
}