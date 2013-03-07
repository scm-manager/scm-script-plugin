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

Sonia.script.Grid = Ext.extend(Sonia.rest.Grid, {
  
  colNameText: 'Name',
  colDescriptionText: 'Description',
  colTypeText: 'Type',
  emptyScriptStoreText: 'No script is available',
  scriptFormTitleText: 'Script Form',
  
  removeTitleText: 'Remove Script',
  removeMsgText: 'Remove Script "{0}"?',
  
  addText: 'Add',
  editText: 'Edit',
  removeText: 'Remove',
  reloadText: 'Reload',
  
  // icons
  addIcon: 'resources/images/add.png',
  editIcon: 'resources/images/edit.png',
  removeIcon: 'resources/images/delete.png',
  reloadIcon: 'resources/images/reload.png',
  
  initComponent: function(){
    var scriptStore = new Sonia.rest.JsonStore({
      proxy: new Ext.data.HttpProxy({
        url: restUrl + 'plugins/script/metadata.json',
        disableCaching: false
      }),
      root: 'script',
      idProperty: 'id',
      fields: [ 'id', 'name', 'description', 'type'],
      sortInfo: {
        field: 'name'
      }
    });

    var scriptColModel = new Ext.grid.ColumnModel({
      defaults: {
        sortable: true,
        scope: this,
        width: 125
      },
      columns: [
        {id: 'name', header: this.colNameText, dataIndex: 'name'},
        {id: 'description', header: this.colDescriptionText, dataIndex: 'description', width: 300 },
        // {id: 'creationDate', header: this.colCreationDateText, dataIndex: 'creationDate', renderer: Ext.util.Format.formatTimestamp},
        {id: 'type', header: this.colTypeText, dataIndex: 'type', width: 120}
      ]
    });

    var config = {
      tbar: [
        {xtype: 'tbbutton', text: this.addText, icon: this.addIcon, scope: this, handler: this.addScript},
        {xtype: 'tbbutton', id: 'scriptEditButton', disabled: true, text: this.editText, icon: this.editIcon, scope: this, handler: this.editScript},
        {xtype: 'tbbutton', id: 'scriptRmButton', disabled: true, text: this.removeText, icon: this.removeIcon, scope: this, handler: this.removeScript},
        '-',
        {xtype: 'tbbutton', text: this.reloadText, icon: this.reloadIcon, scope: this, handler: this.reload}
      ],
      autoExpandColumn: 'description',
      store: scriptStore,
      colModel: scriptColModel,
      emptyText: this.emptyScriptStoreText
    };

    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.script.Grid.superclass.initComponent.apply(this, arguments);
  },
          
  toggleSelectButtons: function(value){
    Ext.getCmp('scriptEditButton').setDisabled(value);
    Ext.getCmp('scriptRmButton').setDisabled(value);    
  },

  selectItem: function(script){
    if ( debug ){
      console.debug( script.id + ' ("' + script.name + '") selected' );
    }
    
    this.toggleSelectButtons(false);
  },
          
  addScript: function(){
    this.toggleSelectButtons(true);
  },
  
  editScript: function(){
    
  },
  
  removeScript: function(){
    var selected = this.getSelectionModel().getSelected();
    if ( selected ){
      var item = selected.data;
      var url = restUrl + 'plugins/script/' + item.id + '.json';

      Ext.MessageBox.show({
        title: this.removeTitleText,
        msg: String.format( this.removeMsgText, item.name ),
        buttons: Ext.MessageBox.OKCANCEL,
        icon: Ext.MessageBox.QUESTION,
        fn: function(result){
          if ( result === 'ok' ){

            if ( debug ){
              console.debug( 'remove script ' + item.name );
            }

            Ext.Ajax.request({
              url: url,
              method: 'DELETE',
              scope: this,
              success: function(){
                this.reload();
                this.resetPanel();
              },
              failure: function(result){
                main.handleRestFailure(
                  result, 
                  this.errorTitleText, 
                  this.errorMsgText
                );
              }
            });
          }

        },
        scope: this
      });

    } else if ( debug ){
      console.debug( 'no script selected' );
    }
  }

});

// register xtype
Ext.reg('scriptGrid', Sonia.script.Grid);
