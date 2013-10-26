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

Ext.ns('Sonia.panel');

Sonia.panel.CodeEditorPanel = Ext.extend(Ext.Panel, {
  
  modeMappingTable: [],
  
  editor: null,
  editorId: null,
  editorTheme: 'ace/theme/textmate',
  editorMode: 'ace/mode/groovy',
  
  initComponent: function(){
    this.modeMappingTable['ace/mode/ecmascript'] = 'ace/mode/javascript';
    
    this.editorId = 'ace-' + Ext.id();
    var config = {
      html: '<div style="width: 100%; height: 100%;" id="' + this.editorId +  '"></div>',
      layout:'fit',
      autoScroll: true,
      listeners: {
        afterlayout: {
          fn: this.resizeEditor,
          scope: this
        },
        afterrender: {
          fn: this.loadBodyContent,
          scope: this
        },
        destroy: {
          fn: this.destroyEditor,
          scope: this
        }
      }
    };
    
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.panel.CodeEditorPanel.superclass.initComponent.apply(this, arguments);
  },
  
  resizeEditor: function(){
    if (this.editor){
      if (debug){
        console.log('resize editor: ' + this.editorId);
      }
      this.editor.resize();
    }
  },
  
  destroyEditor: function(){
    if (this.editor){
      if (debug){
        console.log('destroy editor: ' + this.editorId);
      }
      this.editor.destroy();
    }
  },
          
  getEditorMode: function(){
    var mode = this.editorMode;
    if ( mode.indexOf('ace/mode/') !== 0 ){
      mode = 'ace/mode/' + mode;
    }
    mode = mode.toLowerCase();
    var m = this.modeMappingTable[mode];
    if (!m){
      m = mode;
    }
    return m;
  },
  
  loadBodyContent: function(){
    main.loadScript(
      restUrl + 'plugins/script/static/ace/ace.js', 
      this.renderEditor, 
      this
    );
  },
  
  setMode: function(mode){
    this.editorMode = mode;
    this.editor.getSession().setMode(this.getEditorMode());
  },
  
  renderEditor: function(){
    var editorMode = this.getEditorMode();
    if (debug){
      console.log('render editor ' + this.editorId + ', mode: ' + editorMode + ', theme: ' + this.editorTheme);
    }
    this.editor = ace.edit(this.editorId);
    this.editor.setTheme(this.editorTheme);
    this.editor.getSession().setMode(editorMode);
    this.editor.focus();
    this.fireEvent('editorRendered', this);
  },
  
  getValue: function(){
    return this.editor.getValue();
  },
  
  setValue: function(value){
    this.editor.setValue(value);
  }
  
});

Ext.reg('codeEditorPanel', Sonia.panel.CodeEditorPanel);
