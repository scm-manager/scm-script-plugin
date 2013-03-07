/*
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */
Ext.ns('Sonia.script');

Sonia.script.EditWindow = Ext.extend(Ext.Window,{
  
  script: {},

  titleText: 'Edit Script Metadata',
  
  labelName: 'Name',
  labelDescription: 'Description',
  labelType: 'Type',
  
  cancelText: 'Cancel',
  submitText: 'Submit',
  
  waitTitleText: 'Submit',
  WaitMsgText: 'Submit Metadata...',
  failedMsgText: 'Storing metadata failed!',
  failedDescriptionText: 'The script metadata could not be stored',

  typeStore: null,

  initComponent: function(){
    this.typeStore = Sonia.script.createTypeStore();
    
    var config = {
      layout:'fit',
      width: 300,
      height: 150,
      closable: true,
      resizable: false,
      plain: true,
      border: false,
      modal: true,
      title: this.titleText,
      items: [{
        id: 'scriptMetadataForm',
        xtype: 'form',
        frame: true,
        labelWidth: 80,
        defaultType: 'textfield',
        monitorValid: true,
        listeners: {
          afterrender: function(){
            Ext.getCmp('scriptMetadataName').focus(true, 500);
          }
        },
        items: [{
          id: 'scriptMetadataName',
          name: 'name',
          fieldLabel: this.labelName,
          allowBlank: false
        },{
          id: 'scriptMetadataDescription',
          name: 'description',
          fieldLabel: this.labelDescription,
          allowBlank: true
        },{
          id: 'scriptMetadataType',
          xtype: 'combo',
          name: 'type',
          fieldLabel: this.labelType,
          triggerAction: 'all',
          editable: false,
          displayField: 'name',
          valueField: 'name',
          value: 'Groovy',
          store: this.typeStore
        }],
        buttons: [{
          text: this.cancelText,
          scope: this,
          handler: this.cancel
        },{
          id: 'loginButton',
          text: this.submitText,
          formBind: true,
          scope: this,
          handler: this.submitForm
        }]
      }]
    };

    this.addEvents('store');

    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.login.Window.superclass.initComponent.apply(this, arguments);
  },
  
  cancel: function(){
    this.close();
  },
          
  submitForm: function(){
    var metadata = Ext.getCmp('scriptMetadataForm').getForm().getFieldValues();
    if (debug){
      console.debug('try to store script metadata: ');
      console.debug(metadata);
    }
    var mimetypeRecord = this.typeStore.getById( metadata.type );
    metadata.type = mimetypeRecord.get('mime-type')[0];
    
    var scriptUrl = restUrl + 'plugins/script.json';
    
    Ext.Ajax.request({
      url: scriptUrl,
      method: 'POST',
      jsonData: metadata,
      scope: this,
      success: function(response){
        console.debug('form submitted');
        this.fireEvent('store', metadata);
        this.close();
      },
      failure: function(response){
        Ext.Msg.show({
          title: this.failedMsgText,
          msg: this.failedDescriptionText,
          buttons: Ext.Msg.OK,
          icon: Ext.MessageBox.WARNING
        });
        form.reset();
      }
    });
  }

});