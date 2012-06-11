#!/usr/bin/env python

from gi.repository import Gtk

class DL_Editor(object):
    def __init__(self):
        self.builder = Gtk.Builder()
        self.builder.add_from_file("dl_editor.glade")
        self.builder.connect_signals(self)

    def run(self, *args):
        self.builder.get_object("main_window").show()
        Gtk.main()

    def quit(self, *args):
        Gtk.main_quit()

if __name__=='__main__':
    DL_Editor().run()
