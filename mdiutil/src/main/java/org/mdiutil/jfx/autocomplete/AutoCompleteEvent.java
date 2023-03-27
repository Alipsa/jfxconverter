/*
Copyright (c) 2017, Herve Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies,
either expressed or implied, of the FreeBSD Project.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://sourceforge.net/projects/mdiutilities/
 */
package org.mdiutil.jfx.autocomplete;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import org.mdiutil.text.autocomplete.AutoCompleteDictionary;

/**
 * Represents an Autocomplete Event.
 *
 * @version 0.9.52
 */
public class AutoCompleteEvent extends Event {
   public static final EventType<AutoCompleteEvent> AUTOCOMPLETE = new EventType<>(Event.ANY, "AUTOCOMPLETE");
   private final int startOffset;
   private final AutoCompleteDictionary.Item item;

   /**
    * Constructor.
    *
    * @param startOffset the start offset of the text which was autoCompleted
    * @param item the selected item used for autoCompletion
    */
   public AutoCompleteEvent(int startOffset, AutoCompleteDictionary.Item item) {
      super(AUTOCOMPLETE);
      this.startOffset = startOffset;
      this.item = item;
   }

   public AutoCompleteEvent(Object source, EventTarget target, int startOffset, AutoCompleteDictionary.Item item) {
      super(source, target, AUTOCOMPLETE);
      this.startOffset = startOffset;
      this.item = item;
   }

   @Override
   public AutoCompleteEvent copyFor(Object newSource, EventTarget newTarget) {
      return (AutoCompleteEvent) super.copyFor(newSource, newTarget);
   }

   @Override
   public EventType<? extends AutoCompleteEvent> getEventType() {
      return (EventType<? extends AutoCompleteEvent>) super.getEventType();
   }

   /**
    * Return the start offset of the text which was autoCompleted.
    *
    * @return the start offset of the text
    */
   public int getStartOffset() {
      return startOffset;
   }

   /**
    * Return the selected item used for autoCompletion.
    *
    * @return the selected item
    */
   public AutoCompleteDictionary.Item getItem() {
      return item;
   }

   /**
    * Return the autoCompleted text.
    *
    * @return the text
    */
   public String getText() {
      return item.getText();
   }

}
