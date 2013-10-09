Ext.define('NX.capabilities.view.Settings', {
  extend: 'Ext.form.Panel',
  alias: 'widget.nx-capability-settings',

  title: 'Settings',

  items: {
    xtype: 'nx-capability-settings-fieldset'
  },

  buttons: [
    {
      text: 'Save',
      action: 'save'
    },
    {
      text: 'Cancel'
    }
  ],

  getValues: function(){
    return this.down('nx-capability-settings-fieldset').exportCapability(this.getForm())
  }

});
